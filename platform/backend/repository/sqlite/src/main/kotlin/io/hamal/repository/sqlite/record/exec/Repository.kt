package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ExecRepository
import io.hamal.repository.api.record.exec.ExecEntity
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateExec : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): Exec {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is ExecRecord.Planned)

        var result = ExecEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            namespaceId = firstRecord.namespaceId,
            groupId = firstRecord.groupId,
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
    config: Config
) : RecordSqliteRepository<ExecId, ExecRecord, Exec>(
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

    override fun plan(cmd: PlanCmd): Exec.Planned {
        val execId = cmd.execId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Planned
            } else {
                store(
                    ExecRecord.Planned(
                        cmdId = cmdId,
                        entityId = execId,
                        namespaceId = cmd.namespaceId,
                        groupId = cmd.groupId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        invocation = cmd.invocation
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

                store(ExecRecord.Scheduled(cmdId, execId))

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

                store(ExecRecord.Queued(cmdId, execId))

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

                    store(ExecRecord.Started(cmd.id, execId))

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

                store(ExecRecord.Completed(cmdId, execId, cmd.result, cmd.state))

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

                store(ExecRecord.Failed(cmdId, execId, cmd.result))

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