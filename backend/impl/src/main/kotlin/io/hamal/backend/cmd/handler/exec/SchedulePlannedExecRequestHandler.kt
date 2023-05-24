package io.hamal.backend.cmd.handler.exec

import io.hamal.backend.event.ExecScheduledEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.repository.api.domain.exec.ScheduledExec
import io.hamal.backend.cmd.ExecCmd.SchedulePlannedExec
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import logger

class SchedulePlannedExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<ScheduledExec, SchedulePlannedExec>(SchedulePlannedExec::class) {
    val log = logger(PlanExecRequestHandler::class)

    override fun invoke(useCase: SchedulePlannedExec): ScheduledExec {
        log.debug("Schedule exec: ${useCase.plannedExec}")

        val result = execRequestRepository.schedule(useCase.reqId, useCase.plannedExec)
        eventEmitter.emit(
            ExecScheduledEvent(
                useCase.shard,
                result
            )
        )
        return result
    }

}