package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.CompletedAt
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt

object MemoryExecRepository : ExecCmdRepository, ExecQueryRepository {

    private val execs = mutableMapOf<ExecId, Exec>()

    private val queue = mutableListOf<QueuedExec>()
    private val inFlightExecs = mutableListOf<InFlightExec>()

    override fun plan(commandId: CommandId, execToPlan: ExecCmdRepository.ExecToPlan): PlannedExec {
        return PlannedExec(
            commandId = commandId,
            accountId = execToPlan.accountId,
            id = execToPlan.id,
            correlation = execToPlan.correlation,
            inputs = execToPlan.inputs,
            secrets = execToPlan.secrets,
            code = execToPlan.code,
            invocation = execToPlan.invocation
        ).also { execs[it.id] = it }
    }

    override fun schedule(commandId: CommandId, planedExec: PlannedExec): ScheduledExec {
        return ScheduledExec(
            commandId = commandId,
            id = planedExec.id,
            scheduledAt = ScheduledAt.now(),
            plannedExec = planedExec
        ).also { execs[it.id] = it }
    }

    override fun enqueue(commandId: CommandId, scheduledExec: ScheduledExec): QueuedExec {
        val result = QueuedExec(
            id = scheduledExec.id,
            commandId = commandId,
            queuedAt = QueuedAt.now(),
            scheduledExec = scheduledExec
        ).also { execs[it.id] = it }
        queue.add(result)
        return result
    }

    override fun complete(commandId: CommandId, inFlightExec: InFlightExec): CompletedExec {
        inFlightExecs.removeIf { it.id == inFlightExec.id }
        return CompletedExec(
            id = inFlightExec.id,
            commandId = commandId,
            completedAt = CompletedAt.now(),
            inFlightExec = inFlightExec
        ).also { execs[it.id] = it }
    }


    override fun dequeue(commandId: CommandId): List<InFlightExec> {
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
                    commandId = commandId,
                    queuedExec = it
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