package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.*
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.record.exec.ExecEntity
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.exec.*
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateExec : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): Exec {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is ExecPlannedRecord)

        var result = ExecEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
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
) : SqliteRecordRepository<ExecId, ExecRecord, Exec>(
    config = config,
    createDomainObject = CreateExec,
    recordClass = ExecRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionQueue
    )
), ExecRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "exec.db"
    }

    override fun plan(cmd: PlanCmd): PlannedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as PlannedExec
            } else {
                store(
                    ExecPlannedRecord(
                        cmdId = cmdId,
                        entityId = execId,
                        groupId = cmd.groupId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        codeId = cmd.codeId,
                        codeVersion = cmd.codeVersion,
                        events = cmd.events
                    )
                )

                (currentVersion(execId) as PlannedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as ScheduledExec
            } else {
                check(currentVersion(execId) is PlannedExec) { "$execId not planned" }

                store(ExecScheduledRecord(cmdId, execId))

                (currentVersion(execId) as ScheduledExec).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as QueuedExec
            } else {
                check(currentVersion(execId) is ScheduledExec) { "$execId not scheduled" }

                store(ExecQueuedRecord(cmdId, execId))

                (currentVersion(execId) as QueuedExec)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionQueue.upsert(this, it) }
            }
        }
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        val cmdId = cmd.id
        val result = mutableListOf<StartedExec>()

        tx {
            ProjectionQueue.pop(this, 1).forEach { queuedExec ->
                val execId = queuedExec.id
                if (commandAlreadyApplied(cmdId, execId)) {
                    versionOf(execId, cmdId) as QueuedExec
                } else {
                    check(currentVersion(execId) is QueuedExec) { "$execId not queued" }

                    store(ExecStartedRecord(cmd.id, execId))

                    result.add((currentVersion(execId) as StartedExec).also { ProjectionCurrent.upsert(this, it) })
                }
            }
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as CompletedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "$execId not started" }

                store(ExecCompletedRecord(cmdId, execId))

                (currentVersion(execId) as CompletedExec).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun fail(cmd: FailCmd): FailedExec {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as FailedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "$execId not started" }

                store(ExecFailedRecord(cmdId, execId, cmd.cause))

                (currentVersion(execId) as FailedExec).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(execId: ExecId): Exec? {
        return ProjectionCurrent.find(connection, execId)
    }

    override fun list(query: ExecQuery): List<Exec> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: ExecQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }

}