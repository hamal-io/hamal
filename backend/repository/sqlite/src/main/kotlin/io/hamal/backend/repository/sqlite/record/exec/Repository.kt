package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.*
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.file.Path

class SqliteExecRepository(
    config: Config
) : BaseRepository(config), ExecCmdRepository, ExecQueryRepository {

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
                 cmd_id         NUMERIC NOT NULL,
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (entity_id, sequence),
                 UNIQUE (cmd_id)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalSerializationApi::class)
    override fun plan(cmd: PlanCmd): PlannedExec {

        val execId = cmd.execId


        return connection.tx {
            if (commandAlreadyApplied(execId, cmd.id)) {
                versionOf(execId, cmd.id) as PlannedExec
            } else {

                val r = ExecPlannedRecord(
                    entityId = execId,
                    cmdId = cmd.id,
                    sequence = RecordSequence.first(),
                    correlation = cmd.correlation,
                    inputs = cmd.inputs,
                    secrets = cmd.secrets,
                    code = cmd.code,
                )

                val recs = recordsOf(execId)
                println(recs)
                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
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

                check(currentVersion(execId) is PlannedExec) { "current version of $execId is not planned" }

                val previous = recordsOf(execId).last()

                val r = ExecScheduledRecord(
                    entityId = execId,
                    cmdId = cmdId,
                    sequence = previous.sequence.next()
                )

                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
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
        val execId = cmd.execId
        val cmdId = cmd.id

        return connection.tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as QueuedExec
            } else {

                check(currentVersion(execId) is ScheduledExec) { "current version of $execId is not scheduled" }

                val previous = recordsOf(execId).last()

                val r = ExecQueuedRecord(
                    entityId = execId,
                    cmdId = cmdId,
                    sequence = previous.sequence.next()
                )

                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
                    set("entityId", r.entityId)
                    set("sequence", r.sequence.value)
                    set("data", data) //FIXME serialize record
                }

                (currentVersion(execId) as QueuedExec)
                    .also(CurrentExecProjection::apply)
                    .also(QueueProjection::add)
            }
        }!!
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        val result = mutableListOf<StartedExec>()

        connection.tx {
            QueueProjection.pop(2).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }


                val previous = recordsOf(execId).last()

                val r = ExecStartedRecord(
                    entityId = execId,
                    cmdId = cmd.id,
                    sequence = previous.sequence.next()
                )

                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id,entity_id,sequence, data) 
                    VALUES
                (:cmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
                    set("entityId", r.entityId)
                    set("sequence", r.sequence.value)
                    set("data", data) //FIXME serialize record
                }

                result.add((currentVersion(execId) as StartedExec).also(CurrentExecProjection::apply))
            }
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
        val execId = cmd.execId
        val cmdId = cmd.id

        return connection.tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as CompletedExec
            } else {

                check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }

                val previous = recordsOf(execId).last()

                val r = ExecCompletedRecord(
                    entityId = execId,
                    cmdId = cmdId,
                    sequence = previous.sequence.next()
                )

                val data = ProtoBuf { }.encodeToByteArray<ExecRecord>(r)
                execute(
                    """
                INSERT into records
                (cmd_id, entity_id,sequence, data) 
                    VALUES
                (:cmdId, :entityId, :sequence, :data)
            """.trimIndent()
                ) {
                    set("cmdId", r.cmdId)
                    set("entityId", r.entityId)
                    set("sequence", r.sequence.value)
                    set("data", data) //FIXME serialize record
                }

                (currentVersion(execId) as CompletedExec)
                    .also(CurrentExecProjection::apply)
            }
        }!!
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