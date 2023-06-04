package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.ExecPlannedRecord
import io.hamal.backend.repository.api.record.exec.ExecRecord
import io.hamal.backend.repository.api.record.exec.ExecScheduledRecord
import io.hamal.backend.repository.api.record.exec.createEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.file.Path
import kotlin.io.path.Path

internal object CurrentExecProjection {
    private val projection = mutableMapOf<ExecId, Exec>()
    fun apply(exec: Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): Exec? = projection[execId]

    fun list(afterId: ExecId, limit: Int): List<Exec> {
        return projection.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
    }
}

//FIXME this must be concurrent safe
internal object QueueProjection {
    private val queue = mutableListOf<QueuedExec>()
    fun add(exec: QueuedExec) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<QueuedExec> {
        val result = mutableListOf<QueuedExec>()
        for (idx in 0 until 10) {
            if (queue.isEmpty()) {
                break
            }
            result.add(queue.removeFirst())
        }
        return result
    }
}

fun main() {
    val sqliteExecRepository = SqliteExecRepository(
        SqliteExecRepository.Config(
            path = Path("/tmp/hamal"),
            shard = Shard(1)
        )
    )

    val plannedExec = sqliteExecRepository.plan(
        PlanCmd(
            id = CmdId(1),
//            execId = DefaultDomainIdGenerator(Shard(1), ::ExecId),
            execId = ExecId(1234),
            accountId = AccountId(2),
            inputs = ExecInputs(listOf()),
            secrets = ExecSecrets(listOf()),
            code = Code("Code"),
            invocation = AdhocInvocation(),
            correlation = Correlation(
                funcId = FuncId(234),
                correlationId = CorrelationId("cor")
            )
        )
    )

    println(plannedExec)

    val scheduledExec = sqliteExecRepository.schedule(ScheduleCmd(CmdId(2), ExecId(1234)))

    println(scheduledExec)


}

//FIXME concurrent safety?!
class SqliteExecRepository(config: Config) : BaseRepository(config), ExecCmdRepository, ExecQueryRepository {

    data class Config(
        override val path: Path,
        override val shard: Shard
    ) : BaseRepository.Config {
        override val filename = "exec"
    }

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS records (
                 cmd_id         INTEGER NOT NULL,
                 prev_cmd_id    INTEGER NOT NULL,
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY (cmd_id),
                 UNIQUE (entity_id, sequence),
                 UNIQUE(prev_cmd_id)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        TODO("Not yet implemented")
    }


    @Deprecated("to be replaced")
    internal val store = mutableMapOf<ExecId, MutableList<ExecRecord>>()

    @OptIn(ExperimentalSerializationApi::class)
    override fun plan(cmd: PlanCmd): PlannedExec {

        val execId = cmd.execId


        return connection.tx {
            if (store.containsKey(execId)) {
                versionOf(execId, cmd.id) as PlannedExec
            } else {

                val r = ExecPlannedRecord(
                    entityId = execId,
                    cmdId = cmd.id,
                    prevCmdId = cmd.id,
                    sequence = RecordSequence.first(),
                    correlation = cmd.correlation,
                    inputs = cmd.inputs,
                    secrets = cmd.secrets,
                    code = cmd.code,
                )
                store[execId] = mutableListOf(r)

                val recs = recordsOf(execId)
                println(recs)
                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id,prev_cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :prevCmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
                    set("prevCmdId", r.cmdId)
                    set("entityId", r.entityId)
                    set("sequence", r.sequence.value)
                    set("data", data) //FIXME serialize record
                }

                (currentVersion(execId) as PlannedExec).also(CurrentExecProjection::apply)
            }
        }!!

    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        val execId = cmd.execId
        val cmdId = cmd.id

        return connection.tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as ScheduledExec
            } else {

                val records = checkNotNull(store[execId]) { "No records found for $execId" }
                check(currentVersion(execId) is PlannedExec) { "current version of $execId is not planned" }

                val previous = records.last()

                val r = ExecScheduledRecord(
                    entityId = execId,
                    cmdId = cmdId,
                    prevCmdId = previous.cmdId,
                    sequence = previous.sequence.next()
                )

                records.add(r)

                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id,prev_cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :prevCmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
                    set("prevCmdId", r.cmdId)
                    set("entityId", r.entityId)
                    set("sequence", r.sequence.value)
                    set("data", data) //FIXME serialize record
                }

                (currentVersion(execId) as ScheduledExec)
                    .also(CurrentExecProjection::apply)
            }
        }!!
    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        TODO()
//        val execId = cmd.execId
//        val cmdId = cmd.id
//
//        if (commandAlreadyApplied(execId, cmdId)) {
//            return versionOf(execId, cmdId) as QueuedExec
//        }
//
//        val records = checkNotNull(store[execId]) { "No records found for $execId" }
//        check(currentVersion(execId) is ScheduledExec) { "current version of $execId is not scheduled" }
//
//        val previous = records.last()
//        records.add(
//            ExecQueuedRecord(
//                entityId = execId,
//                cmdId = cmdId,
//                prevCmdId = previous.cmdId,
//                sequence = previous.sequence.next()
//            )
//        )
//
//        return (currentVersion(execId) as QueuedExec)
//            .also(CurrentExecProjection::apply)
//            .also(QueueProjection::add)
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        TODO()
//        val result = mutableListOf<StartedExec>()
//        QueueProjection.pop(2).forEach { queuedExec ->
//            val execId = queuedExec.id
//            val records = checkNotNull(store[execId]) { "No records found for $execId" }
//            check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }
//
//
//            val previous = records.last()
//            records.add(
//                ExecStartedRecord(
//                    entityId = execId,
//                    cmdId = cmd.id,
//                    prevCmdId = previous.cmdId,
//                    sequence = previous.sequence.next()
//                )
//            )
//
//            result.add((currentVersion(execId) as StartedExec).also(CurrentExecProjection::apply))
//        }
//
//        return result
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
//        val execId = cmd.execId
//        val cmdId = cmd.id
//
//        if (commandAlreadyApplied(execId, cmdId)) {
//            return versionOf(execId, cmdId) as CompletedExec
//        }
//
//        val records = checkNotNull(store[execId]) { "No records found for $execId" }
//        check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }
//
//        val previous = records.last()
//        records.add(
//            ExecCompletedRecord(
//                entityId = execId,
//                cmdId = cmdId,
//                prevCmdId = previous.cmdId,
//                sequence = previous.sequence.next()
//            )
//        )
//
//        return (versionOf(execId, cmdId) as CompletedExec)
//            .also(CurrentExecProjection::apply)
        TODO()
    }

    override fun find(execId: ExecId): Exec? {
        return CurrentExecProjection.find(execId)
    }

    override fun list(afterId: ExecId, limit: Int): List<Exec> {
        return CurrentExecProjection.list(afterId, limit)
    }

}

@OptIn(ExperimentalSerializationApi::class)
internal fun Transaction.recordsOf(execId: ExecId): List<ExecRecord> {
    return executeQuery(
        """
        SELECT 
            data
        FROM records 
            WHERE entity_id = :entityId 
        ORDER BY sequence;
    """.trimIndent()
    ) {
        query {
            set("entityId", execId)
        }
        map { rs ->
            ProtoBuf { }.decodeFromByteArray<ExecRecord>(rs.getBytes("data"))
        }
    }
}


internal fun Transaction.currentVersion(execId: ExecId): Exec {
    return recordsOf(execId)
        .createEntity()
        .toDomainObject()
}

internal fun Transaction.commandAlreadyApplied(execId: ExecId, cmdId: CmdId) =
    recordsOf(execId).any { it.cmdId == cmdId }

internal fun Transaction.versionOf(execId: ExecId, cmdId: CmdId): Exec {
    return recordsOf(execId).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}