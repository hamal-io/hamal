package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.*
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.file.Path

class SqliteExecRepository(
    config: Config
) : SqliteRecordRepository<ExecId, ExecRecord>(config, ExecRecord::class), ExecCmdRepository, ExecQueryRepository {

    data class Config(
        override val path: Path,
        override val shard: Shard
    ) : BaseRepository.Config {
        override val filename = "exec"
    }

    override fun plan(cmd: PlanCmd): PlannedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as PlannedExec
            } else {
                storeRecord(
                    ExecPlannedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        sequence = RecordSequence.first(),
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        secrets = cmd.secrets,
                        code = cmd.code,
                    )
                )
                (currentVersion(execId) as PlannedExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as ScheduledExec
            } else {
                val previous = recordsOf(execId).last()
                storeRecord(
                    ExecScheduledRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        sequence = previous.sequence.next()
                    )
                )
                (currentVersion(execId) as ScheduledExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as QueuedExec
            } else {
                val previous = recordsOf(execId).last()
                storeRecord(
                    ExecQueuedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        sequence = previous.sequence.next()
                    )
                )
                (currentVersion(execId) as QueuedExec)
                    .also(CurrentExecProjection::apply)
                    .also(QueueProjection::add)
            }
        }
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        val cmdId = cmd.id
        val result = mutableListOf<StartedExec>()

        tx {
            QueueProjection.pop(2).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }

                if (commandAlreadyApplied(execId, cmdId)) {
                    versionOf(execId, cmdId) as QueuedExec
                } else {
                    val previous = recordsOf(execId).last()
                    storeRecord(
                        ExecStartedRecord(
                            entityId = execId,
                            cmdId = cmd.id,
                            sequence = previous.sequence.next()
                        )
                    )
                    result.add((currentVersion(execId) as StartedExec).also(CurrentExecProjection::apply))
                }
            }
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as CompletedExec
            } else {
                val previous = recordsOf(execId).last()
                storeRecord(
                    ExecCompletedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        sequence = previous.sequence.next()
                    )
                )
                (currentVersion(execId) as CompletedExec).also(CurrentExecProjection::apply)
            }
        }
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