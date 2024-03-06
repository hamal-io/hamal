package io.hamal.repository.memory.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ExecRepository
import io.hamal.repository.api.record.exec.CreateExecFromRecords
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.exec.ExecRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ExecMemoryRepository : RecordMemoryRepository<ExecId, ExecRecord, Exec>(
    createDomainObject = CreateExecFromRecords,
    recordClass = ExecRecord::class,
    projections = listOf(ProjectionCurrent(), ProjectionQueue())
), ExecRepository {

    override fun plan(cmd: PlanCmd): Exec.Planned {
        return lock.withLock {
            val execId = cmd.execId
            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmd.id) as Exec.Planned
            } else {
                store(
                    ExecRecord.Planned(
                        cmdId = cmd.id,
                        entityId = execId,
                        namespaceId = cmd.namespaceId,
                        workspaceId = cmd.workspaceId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        invocation = cmd.invocation
                    )
                )
                (currentVersion(execId) as Exec.Planned).also(currentProjection::upsert)
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): Exec.Scheduled {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmdId) as Exec.Scheduled
            } else {
                check(currentVersion(execId) is Exec.Planned) { "$execId not planned" }

                store(ExecRecord.Scheduled(cmdId, execId))

                (currentVersion(execId) as Exec.Scheduled).also(currentProjection::upsert)
            }
        }
    }

    override fun queue(cmd: QueueCmd): Exec.Queued {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmdId) as Exec.Queued
            } else {
                check(currentVersion(execId) is Exec.Scheduled) { "$execId not scheduled" }

                store(ExecRecord.Queued(cmdId, execId))

                (currentVersion(execId) as Exec.Queued)
                    .also(currentProjection::upsert)
                    .also(queueProjection::upsert)
            }
        }
    }

    override fun start(cmd: StartCmd): List<Exec.Started> {
        return lock.withLock {
            val result = mutableListOf<Exec.Started>()
            queueProjection.pop(1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is Exec.Queued) { "$execId not queued" }

                store(ExecRecord.Started(cmd.id, execId))

                result.add((currentVersion(execId) as Exec.Started).also(currentProjection::upsert))
            }
            result
        }
    }

    override fun close() {}


    override fun complete(cmd: CompleteCmd): Exec.Completed {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Completed
            } else {
                check(currentVersion(execId) is Exec.Started) { "$execId not started" }

                store(ExecRecord.Completed(cmdId, execId, cmd.result, cmd.state))

                (versionOf(execId, cmdId) as Exec.Completed).also(currentProjection::upsert)
            }
        }
    }

    override fun fail(cmd: FailCmd): Exec.Failed {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Failed
            } else {
                check(currentVersion(execId) is Exec.Started) { "$execId not started" }

                store(ExecRecord.Failed(cmdId, execId, cmd.result))

                (versionOf(execId, cmdId) as Exec.Failed).also(currentProjection::upsert)
            }
        }
    }


    override fun find(execId: ExecId): Exec? = lock.withLock { currentProjection.find(execId) }

    override fun list(query: ExecQuery): List<Exec> = lock.withLock { return currentProjection.list(query) }

    override fun count(query: ExecQuery): Count = lock.withLock { return currentProjection.count(query) }

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
    private val queueProjection = getProjection<ProjectionQueue>()
}

