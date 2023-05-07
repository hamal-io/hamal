package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.logger
import io.hamal.backend.notification.JobQueuedNotification
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler

class QueueScheduledJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRepository: JobRepository
) : RequestOneUseCaseHandler<QueuedJob, JobRequest.QueueScheduledJob>(JobRequest.QueueScheduledJob::class) {
    private val log = logger(QueueScheduledJobRequestHandler::class)
    override fun invoke(useCase: JobRequest.QueueScheduledJob): QueuedJob {
        log.debug("Queue scheduled job: ${useCase.scheduledJob}")
        val result = jobRepository.queue(useCase.scheduledJob)
        notifyDomain(
            JobQueuedNotification(
                shard = useCase.shard,
                queuedJob = result
            )
        )

        return result
    }
}