package io.hamal.backend.service.cmd

import io.hamal.backend.event.*
import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.ExecToPlan
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExecCmdService
@Autowired constructor(
    val execCmdRepository: ExecCmdRepository,
    val eventEmitter: EventEmitter,
    val generateDomainId: GenerateDomainId
) {

    fun plan(toPlan: ToPlan): PlannedExec = planExec(toPlan).also(this::emitEvent)

    fun schedule(reqId: ReqId, plannedExec: PlannedExec): ScheduledExec =
        execCmdRepository.schedule(reqId, plannedExec).also { emitEvent(reqId, it) }

    fun enqueue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec =
        execCmdRepository.enqueue(reqId, scheduledExec).also { emitEvent(reqId, it) }

    fun dequeue(reqId: ReqId): List<InFlightExec> =
        execCmdRepository.dequeue(reqId).also { emitEvents(reqId, it) }

    fun complete(reqId: ReqId, inFlightExec: InFlightExec): CompletedExec =
        execCmdRepository.complete(reqId, inFlightExec).also { emitEvent(reqId, it) }

    data class ToPlan(
        val reqId: ReqId,
        val shard: Shard,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
        val invocation: Invocation
    )

}

private fun ExecCmdService.planExec(toPlan: ToPlan): PlannedExec {
    return execCmdRepository.plan(
        toPlan.reqId, ExecToPlan(
            id = generateDomainId(toPlan.shard, ::ExecId),
            shard = toPlan.shard,
            correlation = toPlan.correlation,
            inputs = toPlan.inputs,
            secrets = toPlan.secrets,
            code = toPlan.code,
            trigger = toPlan.invocation
        )
    )
}

private fun ExecCmdService.emitEvent(exec: PlannedExec) {
    eventEmitter.emit(
        ExecPlannedEvent(
//            reqId = exec.reqId,
            shard = exec.shard,
            plannedExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(reqId: ReqId, exec: ScheduledExec) {
    eventEmitter.emit(
        ExecScheduledEvent(
//            reqId = reqId,
            shard = exec.shard,
            scheduledExec = exec
        )
    )
}


private fun ExecCmdService.emitEvent(reqId: ReqId, exec: QueuedExec) {
    eventEmitter.emit(
        ExecutionQueuedEvent(
//            reqId = reqId,
            shard = exec.shard,
            queuedExec = exec
        )
    )
}


private fun ExecCmdService.emitEvents(reqId: ReqId, execs: List<InFlightExec>) {
    execs.forEach {
        eventEmitter.emit(
            ExecutionStartedEvent(
//                reqId = reqId,
                shard = it.shard,
                inFlightExec = it
            )
        )
    }
}


private fun ExecCmdService.emitEvent(reqId: ReqId, exec: CompletedExec) {
    eventEmitter.emit(
        ExecutionCompletedEvent(
//            reqId = reqId,
            shard = exec.shard,
            completedExec = exec
        )
    )
}