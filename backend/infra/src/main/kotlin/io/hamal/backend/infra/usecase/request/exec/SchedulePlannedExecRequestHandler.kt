package io.hamal.backend.infra.usecase.request.exec

import io.hamal.backend.core.exec.ScheduledExec
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecScheduledNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.request.ExecRequest.SchedulePlannedExec
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class SchedulePlannedExecRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<ScheduledExec, SchedulePlannedExec>(SchedulePlannedExec::class) {
    val log = logger(PlanExecRequestHandler::class)

    override fun invoke(useCase: SchedulePlannedExec): ScheduledExec {
        log.debug("Schedule exec: ${useCase.plannedExec}")

        val result = execRequestRepository.schedule(useCase.reqId, useCase.plannedExec)
        notifyDomain(
            ExecScheduledNotification(
                useCase.shard,
                result
            )
        )
        return result
    }

}