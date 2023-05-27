package io.hamal.backend.service.cmd

import io.hamal.backend.event.*
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.service.cmd.ExecCmdService.*
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
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

    fun schedule(toSchedule: ToSchedule): ScheduledExec = scheduleExec(toSchedule).also(this::emitEvent)

    fun enqueue(toEnqueue: ToEnqueue): QueuedExec = enqueueExec(toEnqueue).also(this::emitEvent)

    fun dequeue(toDequeue: ToDequeue): List<StartedExec> = dequeueExec(toDequeue).also(this::emitEvents)

    fun complete(toComplete: ToComplete): CompletedExec = completeExec(toComplete).also(this::emitEvent)

    data class ToPlan(
        val reqId: ReqId,
        val shard: Shard,
        val correlation: Correlation?,
        val code: Code,
        val invocation: Invocation
    )

    data class ToSchedule(
        val reqId: ReqId,
        val plannedExec: PlannedExec
    )

    data class ToEnqueue(
        val reqId: ReqId,
        val scheduledExec: ScheduledExec
    )

    data class ToDequeue(
        val reqId: ReqId,
        val shard: Shard,
    )

    data class ToComplete(
        val reqId: ReqId,
        val shard: Shard,
        val startedExec: StartedExec
    )
}

private fun ExecCmdService.planExec(toPlan: ToPlan): PlannedExec {
    return execCmdRepository.plan(
        toPlan.reqId, ExecCmdRepository.ExecToPlan(
            id = generateDomainId(toPlan.shard, ::ExecId),
            shard = toPlan.shard,
            correlation = toPlan.correlation,
            code = toPlan.code,
            trigger = toPlan.invocation
        )
    )
}

private fun ExecCmdService.emitEvent(exec: PlannedExec) {
    eventEmitter.emit(
        ExecPlannedEvent(
            shard = exec.shard,
            plannedExec = exec
        )
    )
}

private fun ExecCmdService.scheduleExec(toSchedule: ToSchedule): ScheduledExec {
    return execCmdRepository.schedule(toSchedule.reqId, toSchedule.plannedExec)
}

private fun ExecCmdService.emitEvent(exec: ScheduledExec) {
    eventEmitter.emit(
        ExecScheduledEvent(
            shard = exec.shard,
            scheduledExec = exec
        )
    )
}


private fun ExecCmdService.enqueueExec(toEnqueue: ToEnqueue): QueuedExec {
    return execCmdRepository.enqueue(toEnqueue.reqId, toEnqueue.scheduledExec)
}

private fun ExecCmdService.emitEvent(exec: QueuedExec) {
    eventEmitter.emit(
        ExecutionQueuedEvent(
            shard = exec.shard,
            queuedExec = exec
        )
    )
}

private fun ExecCmdService.dequeueExec(toDequeue: ToDequeue): List<StartedExec> {
    return execCmdRepository.dequeue() // FIXME
}

private fun ExecCmdService.emitEvents(execs: List<StartedExec>) {
    execs.forEach {
        eventEmitter.emit(
            ExecutionStartedEvent(
                shard = it.shard,
                startedExec = it
            )
        )
    }
}


private fun ExecCmdService.completeExec(toComplete: ToComplete): CompletedExec {
    return execCmdRepository.complete(toComplete.reqId, toComplete.startedExec)
}

private fun ExecCmdService.emitEvent(exec: CompletedExec) {
    eventEmitter.emit(
        ExecutionCompletedEvent(
            shard = exec.shard,
            completedExec = exec
        )
    )
}