package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobQueuedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.request.JobRequest.QueueScheduledJob
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class QueueScheduledJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRequestRepository: JobRequestRepository
) : RequestOneUseCaseHandler<QueuedJob, QueueScheduledJob>(useCaseClass = QueueScheduledJob::class) {
    private val log = logger(QueueScheduledJobRequestHandler::class)
    override fun invoke(useCase: QueueScheduledJob): QueuedJob {
        log.debug("Queue scheduled job: ${useCase.scheduledJob}")
        val result = jobRequestRepository.queue(useCase.requestId, useCase.scheduledJob)
        notifyDomain(
            JobQueuedNotification(
                shard = useCase.shard,
                queuedJob = result
            )
        )

        return result
    }
}