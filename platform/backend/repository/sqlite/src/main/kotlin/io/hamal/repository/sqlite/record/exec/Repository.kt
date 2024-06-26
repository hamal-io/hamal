package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ExecRepository
import io.hamal.repository.api.record.exec.ExecEntity
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.record.exec.ExecRecord.*
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateExec : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): Exec {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is Planned)

        var result = ExecEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            triggerId = firstRecord.triggerId,
            namespaceId = firstRecord.namespaceId,
            workspaceId = firstRecord.workspaceId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class ExecSqliteRepository(
    path: Path
) : RecordSqliteRepository<ExecId, ExecRecord, Exec>(
    path = path,
    filename = "exec.db",
    createDomainObject = CreateExec,
    recordClass = ExecRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionQueue
    )
), ExecRepository {

    override fun plan(cmd: PlanCmd): Exec.Planned {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Planned
            } else {
                store(
                    Planned(
                        cmdId = cmdId,
                        entityId = execId,
                        triggerId = cmd.triggerId,
                        namespaceId = cmd.namespaceId,
                        workspaceId = cmd.workspaceId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code
                    )
                )

                (currentVersion(execId) as Exec.Planned)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): Exec.Scheduled {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Scheduled
            } else {
                check(currentVersion(execId) is Exec.Planned) { "$execId not planned" }

                store(Scheduled(cmdId, execId))

                (currentVersion(execId) as Exec.Scheduled).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun queue(cmd: QueueCmd): Exec.Queued {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Queued
            } else {
                check(currentVersion(execId) is Exec.Scheduled) { "$execId not scheduled" }

                store(Queued(cmdId, execId))

                (currentVersion(execId) as Exec.Queued)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionQueue.upsert(this, it) }
            }
        }
    }

    override fun start(cmd: StartCmd): List<Exec.Started> {
        val cmdId = cmd.id
        val result = mutableListOf<Exec.Started>()

        tx {
            ProjectionQueue.pop(this, 1).forEach { queuedExec ->
                val execId = queuedExec.id
                if (commandAlreadyApplied(cmdId, execId)) {
                    versionOf(execId, cmdId) as Exec.Queued
                } else {
                    check(currentVersion(execId) is Exec.Queued) { "$execId not queued" }

                    store(Started(cmd.id, execId))

                    result.add((currentVersion(execId) as Exec.Started).also { ProjectionCurrent.upsert(this, it) })
                }
            }
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): Exec.Completed {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Completed
            } else {
                check(currentVersion(execId) is Exec.Started) { "$execId not started" }

                store(Completed(cmdId, execId, cmd.statusCode, cmd.result, cmd.state))

                (currentVersion(execId) as Exec.Completed).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun fail(cmd: FailCmd): Exec.Failed {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Failed
            } else {
                check(currentVersion(execId) is Exec.Started) { "$execId not started" }

                store(Failed(cmdId, execId, cmd.statusCode, cmd.result))

                (currentVersion(execId) as Exec.Failed).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(execId: ExecId): Exec? {
        return ProjectionCurrent.find(connection, execId)
    }

    override fun list(query: ExecQuery): List<Exec> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: ExecQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }

}