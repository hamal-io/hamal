package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.exec.*
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.CompletedAt
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt

object MemoryExecRepository : ExecRequestRepository, ExecQueryRepository {

    private val queue = mutableListOf<QueuedExec>()
    private val startedExecs = mutableListOf<StartedExec>()

    override fun plan(reqId: ReqId, execToPlan: ExecRequestRepository.ExecToPlan): PlannedExec {

        return PlannedExec(
            id = execToPlan.id,
            func = execToPlan.definition,
            trigger = execToPlan.trigger
        )
    }

    override fun schedule(reqId: ReqId, planedExec: PlannedExec): ScheduledExec {
        return ScheduledExec(
            id = planedExec.id,
            func = planedExec.func,
            trigger = planedExec.trigger,
            scheduledAt = ScheduledAt.now()
        )
    }

    override fun queue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec {
        val result = QueuedExec(
            id = scheduledExec.id,
            func = scheduledExec.func,
            trigger = scheduledExec.trigger,
            queuedAt = QueuedAt.now()
        )
        queue.add(result)
        return result
    }

    override fun complete(reqId: ReqId, startedExec: StartedExec): CompleteExec {
        startedExecs.removeIf { it.id == startedExec.id }
        return CompleteExec(
            id = startedExec.id,
            func = startedExec.func,
            trigger = startedExec.trigger,
            completedAt = CompletedAt.now()
        )
    }


    override fun dequeue(): List<StartedExec> {
        if (queue.isEmpty()) {
            return listOf()
        }

        val startedExec = queue.removeFirst().let {
            StartedExec(
                id = it.id,
                func = it.func,
                trigger = it.trigger,
            )
        }

        startedExecs.add(startedExec)

        return listOf(startedExec)
    }

    override fun findStartedExec(execId: ExecId): StartedExec? {
        return startedExecs.find { it.id == execId }
    }
}