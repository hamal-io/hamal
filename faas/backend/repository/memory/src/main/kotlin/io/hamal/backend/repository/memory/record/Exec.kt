package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.backend.repository.api.record.exec.createEntity
import io.hamal.backend.repository.record.exec.*
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.ExecId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentExecProjection {
    private val projection = mutableMapOf<ExecId, Exec>()
    fun apply(exec: Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): Exec? = projection[execId]

    fun list(afterId: ExecId, limit: Limit): List<Exec> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
    }

    fun clear() {
        projection.clear()
    }
}

//FIXME this must be concurrent safe
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

object MemoryExecRepository : BaseRecordRepository<ExecId, ExecRecord>(), ExecCmdRepository, ExecQueryRepository {
    private val lock = ReentrantLock()

    override fun plan(cmd: PlanCmd): PlannedExec {
        return lock.withLock {
            val execId = cmd.execId
            if (contains(execId)) {
                versionOf(execId, cmd.id) as PlannedExec
            } else {

                addRecord(
                    ExecPlannedRecord(
                        entityId = execId,
                        cmdId = cmd.id,
                        correlation = cmd.correlation,
                        inputs = cmd.inputs,
                        code = cmd.code,
                        invocation = cmd.invocation
                    )
                )
                (currentVersion(execId) as PlannedExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as ScheduledExec
            } else {
                check(currentVersion(execId) is PlannedExec) { "current version of $execId is not planned" }

                addRecord(ExecScheduledRecord(execId, cmdId))

                (currentVersion(execId) as ScheduledExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as QueuedExec
            } else {
                check(currentVersion(execId) is ScheduledExec) { "current version of $execId is not scheduled" }

                addRecord(ExecQueuedRecord(execId, cmdId))

                (currentVersion(execId) as QueuedExec)
                    .also(CurrentExecProjection::apply)
                    .also(QueueProjection::add)
            }
        }
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        return lock.withLock {
            val result = mutableListOf<StartedExec>()
            QueueProjection.pop(1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }

                addRecord(ExecStartedRecord(execId, cmd.id))

                result.add((currentVersion(execId) as StartedExec).also(CurrentExecProjection::apply))
            }
            result
        }
    }

    override fun clear() {
        super.clear()
        CurrentExecProjection.clear()
        QueueProjection.clear()
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as CompletedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }

                addRecord(ExecCompletedRecord(execId, cmdId))

                (versionOf(execId, cmdId) as CompletedExec)
                    .also(CurrentExecProjection::apply)
            }
        }
    }

    override fun fail(cmd: FailCmd): FailedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as FailedExec
            } else {
                check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }

                addRecord(ExecFailedRecord(execId, cmdId, cmd.cause))

                (versionOf(execId, cmdId) as FailedExec).also(CurrentExecProjection::apply)
            }
        }
    }


    override fun find(execId: ExecId): Exec? {
        return CurrentExecProjection.find(execId)
    }

    override fun list(block: ExecQuery.() -> Unit): List<Exec> {
        val query = ExecQuery().also(block)
        return CurrentExecProjection.list(query.afterId, query.limit)
    }

    private fun MemoryExecRepository.currentVersion(id: ExecId): Exec {
        return listRecords(id)
            .createEntity()
            .toDomainObject()
    }

    private fun MemoryExecRepository.commandAlreadyApplied(id: ExecId, cmdId: CmdId) =
        listRecords(id).any { it.cmdId == cmdId }

    private fun MemoryExecRepository.versionOf(id: ExecId, cmdId: CmdId): Exec {
        return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
            .createEntity()
            .toDomainObject()
    }
}

