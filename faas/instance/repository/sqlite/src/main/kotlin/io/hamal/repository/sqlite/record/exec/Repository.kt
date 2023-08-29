package io.hamal.repository.sqlite.record.exec

import io.hamal.repository.api.*
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.record.exec.Entity
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.exec.*
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.repository.api.Exec
import io.hamal.repository.record.exec.*
import java.nio.file.Path

internal object CreateExec : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): io.hamal.repository.api.Exec {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is ExecPlannedRecord)

        var result = Entity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteExecRepository(
    config: Config
) : SqliteRecordRepository<ExecId, ExecRecord, io.hamal.repository.api.Exec>(
    config = config,
    createDomainObject = CreateExec,
    recordClass = ExecRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionQueue
    )
), io.hamal.repository.api.ExecRepository {

    data class Config(
        override val path: Path
    ) : BaseSqliteRepository.Config {
        override val filename = "exec.db"
    }

    override fun plan(cmd: PlanCmd): io.hamal.repository.api.PlannedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.PlannedExec
            } else {
                storeRecord(
                    ExecPlannedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        invocation = cmd.invocation
                    )
                )

                (currentVersion(execId) as io.hamal.repository.api.PlannedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): io.hamal.repository.api.ScheduledExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.ScheduledExec
            } else {
                storeRecord(
                    ExecScheduledRecord(
                        entityId = execId,
                        cmdId = cmdId
                    )
                )
                (currentVersion(execId) as io.hamal.repository.api.ScheduledExec)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun queue(cmd: QueueCmd): io.hamal.repository.api.QueuedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.QueuedExec
            } else {
                storeRecord(
                    ExecQueuedRecord(
                        entityId = execId,
                        cmdId = cmdId
                    )
                )

                (currentVersion(execId) as io.hamal.repository.api.QueuedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionQueue.upsert(this, it) }
            }
        }
    }

    override fun start(cmd: StartCmd): List<io.hamal.repository.api.StartedExec> {
        val cmdId = cmd.id
        val result = mutableListOf<io.hamal.repository.api.StartedExec>()

        tx {
            ProjectionQueue.pop(this, 1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is io.hamal.repository.api.QueuedExec) { "current version of $execId is not queued" }

                if (commandAlreadyApplied(execId, cmdId)) {
                    versionOf(execId, cmdId) as io.hamal.repository.api.QueuedExec
                } else {

                    storeRecord(
                        ExecStartedRecord(
                            entityId = execId,
                            cmdId = cmd.id
                        )
                    )
                    result.add(
                        (currentVersion(execId) as io.hamal.repository.api.StartedExec)
                            .also { ProjectionCurrent.upsert(this, it) }
                    )
                }
            }
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): io.hamal.repository.api.CompletedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.CompletedExec
            } else {
                storeRecord(
                    ExecCompletedRecord(
                        entityId = execId,
                        cmdId = cmdId
                    )
                )
                (currentVersion(execId) as io.hamal.repository.api.CompletedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun fail(cmd: FailCmd): io.hamal.repository.api.FailedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.FailedExec
            } else {
                storeRecord(
                    ExecFailedRecord(
                        entityId = execId,
                        cmdId = cmdId,
                        cause = cmd.cause
                    )
                )
                (currentVersion(execId) as io.hamal.repository.api.FailedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(execId: ExecId): io.hamal.repository.api.Exec? {
        return ProjectionCurrent.find(connection, execId)
    }

    override fun list(block: ExecQuery.() -> Unit): List<io.hamal.repository.api.Exec> {
        val query = ExecQuery().also(block)
        return ProjectionCurrent.list(connection, query.afterId, query.limit)
    }

}