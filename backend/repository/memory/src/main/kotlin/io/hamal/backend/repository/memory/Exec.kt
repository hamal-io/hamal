package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.CompletedAt
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt

object MemoryExecRepository : ExecCmdRepository, ExecQueryRepository {

    private val execs = mutableMapOf<ExecId, Exec>()

    private val queue = mutableListOf<QueuedExec>()
    private val inFlightExecs = mutableListOf<InFlightExec>()

    override fun plan(reqId: ReqId, execToPlan: ExecCmdRepository.ExecToPlan): PlannedExec {
        return PlannedExec(
            id = execToPlan.id,
            reqId = reqId,
            correlation = execToPlan.correlation,
            inputs = execToPlan.inputs,
            secrets = execToPlan.secrets,
            code = execToPlan.code,
            invocation = execToPlan.trigger
        ).also { execs[it.id] = it }
    }

    override fun schedule(reqId: ReqId, planedExec: PlannedExec): ScheduledExec {
        return ScheduledExec(
            id = planedExec.id,
            reqId = planedExec.reqId,
            correlation = planedExec.correlation,
            inputs = planedExec.inputs,
            secrets = planedExec.secrets,
            code = planedExec.code,
            invocation = planedExec.invocation,
            scheduledAt = ScheduledAt.now()
        ).also { execs[it.id] = it }
    }

    override fun enqueue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec {
        val result = QueuedExec(
            id = scheduledExec.id,
            reqId = scheduledExec.reqId,
            correlation = scheduledExec.correlation,
            inputs = scheduledExec.inputs,
            secrets = scheduledExec.secrets,
            code = scheduledExec.code,
            invocation = scheduledExec.invocation,
            queuedAt = QueuedAt.now()
        ).also { execs[it.id] = it }
        queue.add(result)
        return result
    }

    override fun complete(reqId: ReqId, inFlightExec: InFlightExec): CompletedExec {
        inFlightExecs.removeIf { it.id == inFlightExec.id }
        return CompletedExec(
            id = inFlightExec.id,
            reqId = inFlightExec.reqId,
            correlation = inFlightExec.correlation,
            inputs = inFlightExec.inputs,
            secrets = inFlightExec.secrets,
            code = inFlightExec.code,
            invocation = inFlightExec.invocation,
            completedAt = CompletedAt.now()
        ).also { execs[it.id] = it }
    }


    override fun dequeue(reqId: ReqId): List<InFlightExec> {
        if (queue.isEmpty()) {
            return listOf()
        }

        val result = mutableListOf<InFlightExec>()
        for (idx in 0 until 10) {
            if (queue.isEmpty()) {
                break
            }
            val inFlightExec = queue.removeFirst().let {
                InFlightExec(
                    id = it.id,
                    reqId = it.reqId,
                    correlation = it.correlation,
                    inputs = it.inputs,
                    secrets = it.secrets,
                    code = it.code,
                    invocation = it.invocation,
                ).also { execs[it.id] = it }
            }

            inFlightExecs.add(inFlightExec)
            result.add(inFlightExec)
        }

        return result
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