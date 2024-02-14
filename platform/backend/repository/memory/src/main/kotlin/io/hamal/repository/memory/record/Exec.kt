package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ExecRepository
import io.hamal.repository.api.record.exec.CreateExecFromRecords
import io.hamal.repository.record.exec.ExecRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object ExecCurrentProjection {
    private val projection = mutableMapOf<ExecId, Exec>()
    fun apply(exec: Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): Exec? = projection[execId]

    fun list(query: ExecQuery): List<Exec> {
        return projection.filter { query.execIds.isEmpty() || it.key in query.execIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: ExecQuery): Count {
        return Count(
            projection.filter { query.execIds.isEmpty() || it.key in query.execIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

//FIXME this must be concurrent extend
internal object QueueProjection {
    private val queue = mutableListOf<Exec.Queued>()
    fun add(exec: Exec.Queued) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<Exec.Queued> {
        val result = mutableListOf<Exec.Queued>()
        for (idx in 0 until limit) {
            if (queue.isEmpty()) {
                break
            }
            result.add(queue.removeFirst())
        }
        return result
    }

    fun clear() {
        queue.clear()
    }
}

class ExecMemoryRepository : RecordMemoryRepository<ExecId, ExecRecord, Exec>(
    createDomainObject = CreateExecFromRecords,
    recordClass = ExecRecord::class
), ExecRepository {
    private val lock = ReentrantLock()

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
                (currentVersion(execId) as Exec.Planned).also(ExecCurrentProjection::apply)
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

                (currentVersion(execId) as Exec.Scheduled).also(ExecCurrentProjection::apply)
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
                    .also(ExecCurrentProjection::apply)
                    .also(QueueProjection::add)
            }
        }
    }

    override fun start(cmd: StartCmd): List<Exec.Started> {
        return lock.withLock {
            val result = mutableListOf<Exec.Started>()
            QueueProjection.pop(1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is Exec.Queued) { "$execId not queued" }

                store(ExecRecord.Started(cmd.id, execId))

                result.add((currentVersion(execId) as Exec.Started).also(ExecCurrentProjection::apply))
            }
            result
        }
    }

    override fun clear() {
        super.clear()
        ExecCurrentProjection.clear()
        QueueProjection.clear()
    }

    override fun close() {
    }


    override fun complete(cmd: CompleteCmd): Exec.Completed {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as Exec.Completed
            } else {
                check(currentVersion(execId) is Exec.Started) { "$execId not started" }

                store(ExecRecord.Completed(cmdId, execId, cmd.result, cmd.state))

                (versionOf(execId, cmdId) as Exec.Completed).also(ExecCurrentProjection::apply)
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

                (versionOf(execId, cmdId) as Exec.Failed).also(ExecCurrentProjection::apply)
            }
        }
    }


    override fun find(execId: ExecId): Exec? = lock.withLock { ExecCurrentProjection.find(execId) }

    override fun list(query: ExecQuery): List<Exec> = lock.withLock { return ExecCurrentProjection.list(query) }

    override fun count(query: ExecQuery): Count = lock.withLock { return ExecCurrentProjection.count(query) }
}

