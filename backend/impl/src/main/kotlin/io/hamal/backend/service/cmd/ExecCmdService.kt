package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.*
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.ExecToPlan
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.domain.CommandId
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

    fun plan(commandId: CommandId, toPlan: ToPlan): PlannedExec =
        planExec(commandId, toPlan).also { emitEvent(commandId, it) }

    fun schedule(commandId: CommandId, plannedExec: PlannedExec): ScheduledExec =
        execCmdRepository.schedule(commandId, plannedExec).also { emitEvent(commandId, it) }

    fun enqueue(commandId: CommandId, scheduledExec: ScheduledExec): QueuedExec =
        execCmdRepository.enqueue(commandId, scheduledExec).also { emitEvent(commandId, it) }

    fun dequeue(commandId: CommandId): List<InFlightExec> =
        execCmdRepository.dequeue(commandId).also { emitEvents(commandId, it) }

    fun complete(commandId: CommandId, inFlightExec: InFlightExec): CompletedExec =
        execCmdRepository.complete(commandId, inFlightExec).also { emitEvent(commandId, it) }

    data class ToPlan(
        val execId: ExecId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
        val invocation: Invocation
    )

}

private fun ExecCmdService.planExec(commandId: CommandId, toPlan: ToPlan): PlannedExec {
    return execCmdRepository.plan(
        commandId, ExecToPlan(
            id = toPlan.execId,
            correlation = toPlan.correlation,
            accountId = toPlan.invocation.invokedBy.value, // FIXME
            inputs = toPlan.inputs,
            secrets = toPlan.secrets,
            code = toPlan.code,
            invocation = toPlan.invocation
        )
    )
}

private fun ExecCmdService.emitEvent(commandId: CommandId, exec: PlannedExec) {
    eventEmitter.emit(
        commandId, ExecPlannedEvent(
            shard = exec.shard,
            plannedExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(commandId: CommandId, exec: ScheduledExec) {
    eventEmitter.emit(
        commandId, ExecScheduledEvent(
            shard = exec.shard,
            scheduledExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(commandId: CommandId, exec: QueuedExec) {
    eventEmitter.emit(
        commandId, ExecutionQueuedEvent(
            shard = exec.shard,
            queuedExec = exec
        )
    )
}

private fun ExecCmdService.emitEvents(commandId: CommandId, execs: List<InFlightExec>) {
    execs.forEach {
        eventEmitter.emit(
            commandId, ExecutionStartedEvent(
                shard = it.shard,
                inFlightExec = it
            )
        )
    }
}


private fun ExecCmdService.emitEvent(commandId: CommandId, exec: CompletedExec) {
    eventEmitter.emit(
        commandId, ExecutionCompletedEvent(
            shard = exec.shard,
            completedExec = exec
        )
    )
}