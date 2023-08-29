package io.hamal.repository.memory.record

import io.hamal.backend.repository.api.*
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.backend.repository.api.record.exec.createEntity
import io.hamal.backend.repository.record.exec.*
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.record.exec.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentExecProjection {
    private val projection = mutableMapOf<ExecId, io.hamal.repository.api.Exec>()
    fun apply(exec: io.hamal.repository.api.Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): io.hamal.repository.api.Exec? = projection[execId]

    fun list(afterId: ExecId, limit: Limit): List<io.hamal.repository.api.Exec> {
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
    private val queue = mutableListOf<io.hamal.repository.api.QueuedExec>()
    fun add(exec: io.hamal.repository.api.QueuedExec) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<io.hamal.repository.api.QueuedExec> {
        val result = mutableListOf<io.hamal.repository.api.QueuedExec>()
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

object MemoryExecRepository : BaseRecordRepository<ExecId, ExecRecord>(), io.hamal.repository.api.ExecRepository {
    private val lock = ReentrantLock()

    override fun plan(cmd: PlanCmd): io.hamal.repository.api.PlannedExec {
        return lock.withLock {
            val execId = cmd.execId
            if (contains(execId)) {
                versionOf(execId, cmd.id) as io.hamal.repository.api.PlannedExec
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
                (currentVersion(execId) as io.hamal.repository.api.PlannedExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun schedule(cmd: ScheduleCmd): io.hamal.repository.api.ScheduledExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.ScheduledExec
            } else {
                check(currentVersion(execId) is io.hamal.repository.api.PlannedExec) { "current version of $execId is not planned" }

                addRecord(ExecScheduledRecord(execId, cmdId))

                (currentVersion(execId) as io.hamal.repository.api.ScheduledExec).also(CurrentExecProjection::apply)
            }
        }
    }

    override fun queue(cmd: QueueCmd): io.hamal.repository.api.QueuedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.QueuedExec
            } else {
                check(currentVersion(execId) is io.hamal.repository.api.ScheduledExec) { "current version of $execId is not scheduled" }

                addRecord(ExecQueuedRecord(execId, cmdId))

                (currentVersion(execId) as io.hamal.repository.api.QueuedExec)
                    .also(CurrentExecProjection::apply)
                    .also(QueueProjection::add)
            }
        }
    }

    override fun start(cmd: StartCmd): List<io.hamal.repository.api.StartedExec> {
        return lock.withLock {
            val result = mutableListOf<io.hamal.repository.api.StartedExec>()
            QueueProjection.pop(1).forEach { queuedExec ->
                val execId = queuedExec.id
                check(currentVersion(execId) is io.hamal.repository.api.QueuedExec) { "current version of $execId is not queued" }

                addRecord(ExecStartedRecord(execId, cmd.id))

                result.add((currentVersion(execId) as io.hamal.repository.api.StartedExec).also(CurrentExecProjection::apply))
            }
            result
        }
    }

    override fun clear() {
        super.clear()
        CurrentExecProjection.clear()
        QueueProjection.clear()
    }


    override fun complete(cmd: CompleteCmd): io.hamal.repository.api.CompletedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.CompletedExec
            } else {
                check(currentVersion(execId) is io.hamal.repository.api.StartedExec) { "current version of $execId is not started" }

                addRecord(ExecCompletedRecord(execId, cmdId))

                (versionOf(execId, cmdId) as io.hamal.repository.api.CompletedExec)
                    .also(CurrentExecProjection::apply)
            }
        }
    }

    override fun fail(cmd: FailCmd): io.hamal.repository.api.FailedExec {
        return lock.withLock {
            val execId = cmd.execId
            val cmdId = cmd.id

            if (commandAlreadyApplied(execId, cmdId)) {
                versionOf(execId, cmdId) as io.hamal.repository.api.FailedExec
            } else {
                check(currentVersion(execId) is io.hamal.repository.api.StartedExec) { "current version of $execId is not started" }

                addRecord(ExecFailedRecord(execId, cmdId, cmd.cause))

                (versionOf(execId, cmdId) as io.hamal.repository.api.FailedExec).also(CurrentExecProjection::apply)
            }
        }
    }


    override fun find(execId: ExecId): io.hamal.repository.api.Exec? {
        return CurrentExecProjection.find(execId)
    }

    override fun list(block: ExecQuery.() -> Unit): List<io.hamal.repository.api.Exec> {
        val query = ExecQuery().also(block)
        return CurrentExecProjection.list(query.afterId, query.limit)
    }

    private fun MemoryExecRepository.currentVersion(id: ExecId): io.hamal.repository.api.Exec {
        return listRecords(id)
            .createEntity()
            .toDomainObject()
    }

    private fun MemoryExecRepository.commandAlreadyApplied(id: ExecId, cmdId: CmdId) =
        listRecords(id).any { it.cmdId == cmdId }

    private fun MemoryExecRepository.versionOf(id: ExecId, cmdId: CmdId): io.hamal.repository.api.Exec {
        return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
            .createEntity()
            .toDomainObject()
    }
}

