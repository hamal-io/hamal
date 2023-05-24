package io.hamal.backend.infra.usecase.request.exec

import io.hamal.backend.core.exec.CompleteExec
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecutionCompletedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.request.ExecRequest
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CompleteStartedExecRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<CompleteExec, ExecRequest.CompleteStartedExec>(ExecRequest.CompleteStartedExec::class) {
    private val log = logger(CompleteStartedExecRequestHandler::class)
    override fun invoke(useCase: ExecRequest.CompleteStartedExec): CompleteExec {
        log.debug("Complete started exec: ${useCase.startedExec}")
        val result = execRequestRepository.complete(useCase.reqId, useCase.startedExec)
        notifyDomain(
            ExecutionCompletedNotification(
                useCase.shard,
                result
            )
        )
        return result
    }

}