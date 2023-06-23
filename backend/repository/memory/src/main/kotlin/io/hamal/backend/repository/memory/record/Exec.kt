package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.createEntity
import io.hamal.backend.repository.record.exec.*
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.ExecId

internal object CurrentExecProjection {
    private val projection = mutableMapOf<ExecId, Exec>()
    fun apply(exec: Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): Exec? = projection[execId]

    fun list(afterId: ExecId, limit: Int): List<Exec> {
        return projection.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
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
}

object MemoryExecRepository : BaseRecordRepository<ExecId, ExecRecord>(), ExecCmdRepository, ExecQueryRepository {

    override fun plan(cmd: PlanCmd): PlannedExec {
        val execId = cmd.execId

        if (contains(execId)) {
            return versionOf(execId, cmd.id) as PlannedExec
        }

        addRecord(
            ExecPlannedRecord(
                entityId = execId,
                cmdId = cmd.id,
                correlation = cmd.correlation,
                inputs = cmd.inputs,
                secrets = cmd.secrets,
                code = cmd.code
            )
        )

        return (currentVersion(execId) as PlannedExec)
            .also(CurrentExecProjection::apply)
    }

    override fun schedule(cmd: ScheduleCmd): ScheduledExec {
        val execId = cmd.execId
        val cmdId = cmd.id

        if (commandAlreadyApplied(execId, cmdId)) {
            return versionOf(execId, cmdId) as ScheduledExec
        }

        check(currentVersion(execId) is PlannedExec) { "current version of $execId is not planned" }

        addRecord(
            ExecScheduledRecord(
                entityId = execId,
                cmdId = cmdId
            )
        )

        return (currentVersion(execId) as ScheduledExec)
            .also(CurrentExecProjection::apply)

    }

    override fun queue(cmd: QueueCmd): QueuedExec {
        val execId = cmd.execId
        val cmdId = cmd.id

        if (commandAlreadyApplied(execId, cmdId)) {
            return versionOf(execId, cmdId) as QueuedExec
        }

        check(currentVersion(execId) is ScheduledExec) { "current version of $execId is not scheduled" }

        addRecord(
            ExecQueuedRecord(
                entityId = execId,
                cmdId = cmdId
            )
        )

        return (currentVersion(execId) as QueuedExec)
            .also(CurrentExecProjection::apply)
            .also(QueueProjection::add)
    }

    override fun start(cmd: StartCmd): List<StartedExec> {
        val result = mutableListOf<StartedExec>()
        QueueProjection.pop(2).forEach { queuedExec ->
            val execId = queuedExec.id
            check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }

            addRecord(
                ExecStartedRecord(
                    entityId = execId,
                    cmdId = cmd.id
                )
            )

            result.add((currentVersion(execId) as StartedExec).also(CurrentExecProjection::apply))
        }

        return result
    }


    override fun complete(cmd: CompleteCmd): CompletedExec {
        val execId = cmd.execId
        val cmdId = cmd.id

        if (commandAlreadyApplied(execId, cmdId)) {
            return versionOf(execId, cmdId) as CompletedExec
        }

        check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }

        addRecord(
            ExecCompletedRecord(
                entityId = execId,
                cmdId = cmdId
            )
        )

        return (versionOf(execId, cmdId) as CompletedExec)
            .also(CurrentExecProjection::apply)
    }

    override fun find(execId: ExecId): Exec? {
        return CurrentExecProjection.find(execId)
    }

    override fun list(afterId: ExecId, limit: Int): List<Exec> {
        return CurrentExecProjection.list(afterId, limit)
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


