package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.*
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.ExecToPlan
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExecCmdService(
    @Autowired val execCmdRepository: ExecCmdRepository,
    @Autowired val eventEmitter: EventEmitter
) {

    fun plan(computeId: ComputeId, toPlan: ToPlan): PlannedExec =
        planExec(computeId, toPlan).also { emitEvent(computeId, it) }

    fun schedule(computeId: ComputeId, plannedExec: PlannedExec): ScheduledExec =
        execCmdRepository.schedule(computeId, plannedExec).also { emitEvent(computeId, it) }

    fun enqueue(computeId: ComputeId, scheduledExec: ScheduledExec): QueuedExec =
        execCmdRepository.enqueue(computeId, scheduledExec).also { emitEvent(computeId, it) }

    fun dequeue(computeId: ComputeId): List<InFlightExec> =
        execCmdRepository.dequeue(computeId).also { emitEvents(computeId, it) }

    fun complete(computeId: ComputeId, inFlightExec: InFlightExec): CompletedExec =
        execCmdRepository.complete(computeId, inFlightExec).also { emitEvent(computeId, it) }

    data class ToPlan(
        val execId: ExecId,
        val shard: Shard,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
        val invocation: Invocation
    )

}

private fun ExecCmdService.planExec(computeId: ComputeId, toPlan: ToPlan): PlannedExec {
    return execCmdRepository.plan(
        computeId, ExecToPlan(
            id = toPlan.execId,
            shard = toPlan.shard,
            correlation = toPlan.correlation,
            inputs = toPlan.inputs,
            secrets = toPlan.secrets,
            code = toPlan.code,
            trigger = toPlan.invocation
        )
    )
}

private fun ExecCmdService.emitEvent(computeId: ComputeId, exec: PlannedExec) {
    eventEmitter.emit(
        computeId, ExecPlannedEvent(
            shard = exec.shard,
            plannedExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(computeId: ComputeId, exec: ScheduledExec) {
    eventEmitter.emit(
        computeId, ExecScheduledEvent(
            shard = exec.shard,
            scheduledExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(computeId: ComputeId, exec: QueuedExec) {
    eventEmitter.emit(
        computeId, ExecutionQueuedEvent(
            shard = exec.shard,
            queuedExec = exec
        )
    )
}

private fun ExecCmdService.emitEvents(computeId: ComputeId, execs: List<InFlightExec>) {
    execs.forEach {
        eventEmitter.emit(
            computeId, ExecutionStartedEvent(
                shard = it.shard,
                inFlightExec = it
            )
        )
    }
}


private fun ExecCmdService.emitEvent(computeId: ComputeId, exec: CompletedExec) {
    eventEmitter.emit(
        computeId, ExecutionCompletedEvent(
            shard = exec.shard,
            completedExec = exec
        )
    )
}