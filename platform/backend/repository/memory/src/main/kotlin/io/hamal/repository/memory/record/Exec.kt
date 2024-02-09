package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.*
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.record.exec.CreateExecFromRecords
import io.hamal.repository.record.exec.*
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
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
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
                .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
                .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
                .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
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
    private val queue = mutableListOf<QueuedExec>()
    fun add(exec: QueuedExec) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<QueuedExec> {
        val result = mutableListOf<QueuedExec>()
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

    override fun plan(cmd: PlanCmd): PlannedExec {
        return lock.withLock {
            val execId = cmd.execId
            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmd.id) as PlannedExec
            } else {
                store(
                    ExecPlannedRecord(
                        cmdId = cmd.id,
                        entityId = execId,
                        flowId = cmd.flowId,
                        groupId = cmd.groupId,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        invocation = cmd.invocation
                    )
                )
                (currentVersion(execId) as PlannedExec).also(ExecCurrentProjection::apply)
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmdId) as ScheduledExec
            } else {
                check(currentVersion(execId) is PlannedExec) { "$execId not planned" }

                store(ExecScheduledRecord(cmdId, execId))

                (currentVersion(execId) as ScheduledExec).also(ExecCurrentProjection::apply)
            }
        }
    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmd.id, execId)) {
                versionOf(execId, cmdId) as QueuedExec
            } else {
                check(currentVersion(execId) is ScheduledExec) { "$execId not scheduled" }

                store(ExecQueuedRecord(cmdId, execId))

                (currentVersion(execId) as QueuedExec)
                    .also(ExecCurrentProjection::apply)
                    .also(QueueProjection::add)
            }
        }
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        return lock.withLock {
            val result = mutableListOf<StartedExec>()
            QueueProjection.pop(1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is QueuedExec) { "$execId not queued" }

                store(ExecStartedRecord(cmd.id, execId))

                result.add((currentVersion(execId) as StartedExec).also(ExecCurrentProjection::apply))
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


    override fun complete(cmd: CompleteCmd): CompletedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as CompletedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "$execId not started" }

                store(ExecCompletedRecord(cmdId, execId, cmd.result, cmd.state))

                (versionOf(execId, cmdId) as CompletedExec).also(ExecCurrentProjection::apply)
            }
        }
    }

    override fun fail(cmd: FailCmd): FailedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(cmdId, execId)) {
                versionOf(execId, cmdId) as FailedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "$execId not started" }

                store(ExecFailedRecord(cmdId, execId, cmd.result))

                (versionOf(execId, cmdId) as FailedExec).also(ExecCurrentProjection::apply)
            }
        }
    }


    override fun find(execId: ExecId): Exec? = lock.withLock { ExecCurrentProjection.find(execId) }

    override fun list(query: ExecQuery): List<Exec> = lock.withLock { return ExecCurrentProjection.list(query) }

    override fun count(query: ExecQuery): Count = lock.withLock { return ExecCurrentProjection.count(query) }
}

