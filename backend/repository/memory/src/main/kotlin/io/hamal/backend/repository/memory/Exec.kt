package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.exec.*
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.CompletedAt
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt

object MemoryExecRepository : ExecCmdRepository, ExecQueryRepository {

    private val execs = mutableMapOf<ExecId, Exec>()

    private val queue = mutableListOf<QueuedExec>()
    private val startedExecs = mutableListOf<StartedExec>()

    override fun plan(reqId: ReqId, execToPlan: ExecCmdRepository.ExecToPlan): PlannedExec {
        return PlannedExec(
            id = execToPlan.id,
            code = execToPlan.code,
            invokedTrigger = execToPlan.trigger
        ).also { execs[it.id] = it }
    }

    override fun schedule(reqId: ReqId, planedExec: PlannedExec): ScheduledExec {
        return ScheduledExec(
            id = planedExec.id,
            code = planedExec.code,
            invokedTrigger = planedExec.invokedTrigger,
            scheduledAt = ScheduledAt.now()
        ).also { execs[it.id] = it }
    }

    override fun queue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec {
        val result = QueuedExec(
            id = scheduledExec.id,
            code = scheduledExec.code,
            invokedTrigger = scheduledExec.invokedTrigger,
            queuedAt = QueuedAt.now()
        ).also { execs[it.id] = it }
        queue.add(result)
        return result
    }

    override fun complete(reqId: ReqId, startedExec: StartedExec): CompleteExec {
        startedExecs.removeIf { it.id == startedExec.id }
        return CompleteExec(
            id = startedExec.id,
            code = startedExec.code,
            invokedTrigger = startedExec.invokedTrigger,
            completedAt = CompletedAt.now()
        ).also { execs[it.id] = it }
    }


    override fun dequeue(): List<StartedExec> {
        if (queue.isEmpty()) {
            return listOf()
        }

        val startedExec = queue.removeFirst().let {
            StartedExec(
                id = it.id,
                code = it.code,
                invokedTrigger = it.invokedTrigger,
            ).also { execs[it.id] = it }
        }

        startedExecs.add(startedExec)

        return listOf(startedExec)
    }

    override fun find(execId: ExecId): Exec? {
        return execs[execId]
    }

    override fun list(afterId: ExecId, limit: Int): List<Exec> {
        return execs.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .map { execs.get(it)!! }
            .reversed()
    }
}