package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.*
import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.ExecId
import java.nio.file.Path

internal object CreateExec : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(records: List<ExecRecord>): Exec {
        check(records.isNotEmpty()) { "At least one record is required" }
        val firstRecord = records.first()
        check(firstRecord is ExecPlannedRecord)

        var result = Entity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence()
        )

        records.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteExecRepository(
    config: Config
) : SqliteRecordRepository<ExecId, ExecRecord, Exec>(
    config = config,
    createDomainObject = CreateExec,
    recordClass = ExecRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionQueue
    )
), ExecCmdRepository,
    ExecQueryRepository {

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
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        secrets = cmd.secrets,
                        code = cmd.code,
                    )
                )

                (currentVersion(execId) as PlannedExec)
                    .also { ProjectionCurrent.update(this, it) }
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
                storeRecord(
                    ExecScheduledRecord(
                        entityId = execId,
                        cmdId = cmdId,
                    )
                )
                (currentVersion(execId) as ScheduledExec)
                    .also { ProjectionCurrent.update(this, it) }
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
                storeRecord(
                    ExecQueuedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                    )
                )

                (currentVersion(execId) as QueuedExec)
                    .also { ProjectionCurrent.update(this, it) }
                    .also { ProjectionQueue.update(this, it) }
            }
        }
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        val cmdId = cmd.id
        val result = mutableListOf<StartedExec>()

        tx {
            ProjectionQueue.pop(this, 2).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }

                if (commandAlreadyApplied(execId, cmdId)) {
                    versionOf(execId, cmdId) as QueuedExec
                } else {

                    storeRecord(
                        ExecStartedRecord(
                            entityId = execId,
                            cmdId = cmd.id,
                        )
                    )
                    result.add(
                        (currentVersion(execId) as StartedExec)
                            .also { ProjectionCurrent.update(this, it) }
                    )
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
                storeRecord(
                    ExecCompletedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                    )
                )
                (currentVersion(execId) as CompletedExec)
                    .also { ProjectionCurrent.update(this, it) }
            }
        }
    }

    override fun find(execId: ExecId): Exec? {
        return ProjectionCurrent.find(connection, execId)
    }

    override fun list(afterId: ExecId, limit: Int): List<Exec> {
        return ProjectionCurrent.list(connection, afterId, limit)
    }

}