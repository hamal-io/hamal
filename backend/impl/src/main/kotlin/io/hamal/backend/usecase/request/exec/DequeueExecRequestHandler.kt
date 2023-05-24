package io.hamal.backend.usecase.request.exec

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.notification.ExecutionStartedNotification
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.repository.api.domain.exec.StartedExec
import io.hamal.lib.domain.ddd.RequestManyUseCaseHandler
import logger

class DequeueExecRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val execRequestRepository: ExecRequestRepository
) : RequestManyUseCaseHandler<StartedExec, ExecRequest.DequeueExec>(ExecRequest.DequeueExec::class) {
    private val log = logger(DequeueExecRequestHandler::class)
    override fun invoke(useCase: ExecRequest.DequeueExec): List<StartedExec> {
        log.debug("Dequeue exec")
        val result = execRequestRepository.dequeue()

        result.forEach {
            notifyDomain(
                ExecutionStartedNotification(
                    shard = useCase.shard,
                    startedExec = it
                )
            )
        }
        return result
    }
}