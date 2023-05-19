package io.hamal.backend.usecase.request.exec

import io.hamal.backend.core.exec.QueuedExec
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecutionQueuedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.usecase.request.ExecRequest.QueueScheduledExec
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class QueueScheduledExecRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<QueuedExec, QueueScheduledExec>(useCaseClass = QueueScheduledExec::class) {
    private val log = logger(QueueScheduledExecRequestHandler::class)
    override fun invoke(useCase: QueueScheduledExec): QueuedExec {
        log.debug("Queue scheduled exec: ${useCase.scheduledExec}")
        val result = execRequestRepository.queue(useCase.reqId, useCase.scheduledExec)
        notifyDomain(
            ExecutionQueuedNotification(
                shard = useCase.shard,
                queuedExec = result
            )
        )

        return result
    }
}