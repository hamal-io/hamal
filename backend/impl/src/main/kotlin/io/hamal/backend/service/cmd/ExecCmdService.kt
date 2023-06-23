package io.hamal.backend.service.cmd

import io.hamal.backend.component.SystemEventEmitter
import io.hamal.backend.event.*
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.PlanCmd
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExecCmdService(
    @Autowired val execCmdRepository: ExecCmdRepository,
    @Autowired val eventEmitter: SystemEventEmitter<*>
) {

    fun plan(cmdId: CmdId, toPlan: ToPlan): PlannedExec =
        planExec(cmdId, toPlan).also { emitEvent(cmdId, it) }

    fun schedule(cmdId: CmdId, plannedExec: PlannedExec): ScheduledExec =
        execCmdRepository.schedule(ExecCmdRepository.ScheduleCmd(cmdId, plannedExec.id)).also { emitEvent(cmdId, it) }

    fun queue(cmdId: CmdId, scheduledExec: ScheduledExec): QueuedExec =
        execCmdRepository.queue(ExecCmdRepository.QueueCmd(cmdId, scheduledExec.id)).also { emitEvent(cmdId, it) }

    fun start(cmdId: CmdId): List<StartedExec> =
        execCmdRepository.start(ExecCmdRepository.StartCmd(cmdId)).also { emitEvents(cmdId, it) }

    fun complete(cmdId: CmdId, startedExec: StartedExec): CompletedExec =
        execCmdRepository.complete(ExecCmdRepository.CompleteCmd(cmdId, startedExec.id)).also { emitEvent(cmdId, it) }

    data class ToPlan(
        val execId: ExecId,
        val tenantId: TenantId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
    )

}

private fun ExecCmdService.planExec(cmdId: CmdId, toPlan: ToPlan): PlannedExec {
    return execCmdRepository.plan(
        PlanCmd(
            id = cmdId,
            execId = toPlan.execId,
            correlation = toPlan.correlation,
            tenantId = toPlan.tenantId,
            inputs = toPlan.inputs,
            secrets = toPlan.secrets,
            code = toPlan.code,
        )
    )
}

private fun ExecCmdService.emitEvent(cmdId: CmdId, exec: PlannedExec) {
    eventEmitter.emit(
        cmdId, ExecPlannedEvent(
            shard = exec.shard,
            plannedExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(cmdId: CmdId, exec: ScheduledExec) {
    eventEmitter.emit(
        cmdId, ExecScheduledEvent(
            shard = exec.shard,
            scheduledExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(cmdId: CmdId, exec: QueuedExec) {
    eventEmitter.emit(
        cmdId, ExecutionQueuedEvent(
            shard = exec.shard,
            queuedExec = exec
        )
    )
}

private fun ExecCmdService.emitEvents(cmdId: CmdId, execs: List<StartedExec>) {
    execs.forEach {
        eventEmitter.emit(
            cmdId, ExecutionStartedEvent(
                shard = it.shard,
                startedExec = it
            )
        )
    }
}


private fun ExecCmdService.emitEvent(cmdId: CmdId, exec: CompletedExec) {
    eventEmitter.emit(
        cmdId, ExecutionCompletedEvent(
            shard = exec.shard,
            completedExec = exec
        )
    )
}