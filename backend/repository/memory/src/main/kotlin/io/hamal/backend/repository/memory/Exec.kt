package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.api.record.exec.*
import io.hamal.backend.repository.record.RecordSequence
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
        for (idx in 0 until 10) {
            if (queue.isEmpty()) {
                break
            }
            result.add(queue.removeFirst())
        }
        return result
    }
}

//FIXME concurrent safety?!
object MemoryExecRepository : ExecCmdRepository, ExecQueryRepository {

    internal val store = mutableMapOf<ExecId, MutableList<ExecRecord>>()

    override fun plan(cmd: PlanCmd): PlannedExec {

        val execId = cmd.execId

        if (store.containsKey(execId)) {
            return versionOf(execId, cmd.id) as PlannedExec
        }
        store[execId] = mutableListOf(
            ExecPlannedRecord(
                id = execId,
                cmdId = cmd.id,
                prevCmdId = cmd.id,
                sequence = RecordSequence.first(),
                correlation = cmd.correlation,
                inputs = cmd.inputs,
                secrets = cmd.secrets,
                code = cmd.code,
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

        val records = checkNotNull(store[execId]) { "No records found for $execId" }
        check(currentVersion(execId) is PlannedExec) { "current version of $execId is not planned" }

        val previous = records.last()
        records.add(
            ExecScheduledRecord(
                id = execId,
                cmdId = cmdId,
                prevCmdId = previous.cmdId,
                sequence = previous.sequence.next()
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

        val records = checkNotNull(store[execId]) { "No records found for $execId" }
        check(currentVersion(execId) is ScheduledExec) { "current version of $execId is not scheduled" }

        val previous = records.last()
        records.add(
            ExecQueuedRecord(
                id = execId,
                cmdId = cmdId,
                prevCmdId = previous.cmdId,
                sequence = previous.sequence.next()
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
            val records = checkNotNull(store[execId]) { "No records found for $execId" }
            check(currentVersion(execId) is QueuedExec) { "current version of $execId is not queued" }


            val previous = records.last()
            records.add(
                ExecStartedRecord(
                    id = execId,
                    cmdId = cmd.id,
                    prevCmdId = previous.cmdId,
                    sequence = previous.sequence.next()
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

        val records = checkNotNull(store[execId]) { "No records found for $execId" }
        check(currentVersion(execId) is StartedExec) { "current version of $execId is not started" }

        val previous = records.last()
        records.add(
            ExecCompletedRecord(
                id = execId,
                cmdId = cmdId,
                prevCmdId = previous.cmdId,
                sequence = previous.sequence.next()
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
}


internal fun MemoryExecRepository.currentVersion(id: ExecId): Exec {
    val records = store[id]
    checkNotNull(records) { "No records found for $id" }
    return records
        .createEntity()
        .toDomainObject()
}

internal fun MemoryExecRepository.commandAlreadyApplied(id: ExecId, cmdId: CmdId) =
    store.getOrDefault(id, listOf()).any { it.cmdId == cmdId }

internal fun MemoryExecRepository.versionOf(id: ExecId, cmdId: CmdId): Exec {
    val records = store[id]
    requireNotNull(records) { "No records found for $id" }
    return records.takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}