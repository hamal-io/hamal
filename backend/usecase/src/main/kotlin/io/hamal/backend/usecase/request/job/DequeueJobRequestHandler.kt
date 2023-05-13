package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.StartedJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobStartedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.domain.ddd.RequestManyUseCaseHandler

class DequeueJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRequestRepository: JobRequestRepository
) : RequestManyUseCaseHandler<StartedJob, JobRequest.DequeueJob>(JobRequest.DequeueJob::class) {
    private val log = logger(DequeueJobRequestHandler::class)
    override fun invoke(useCase: JobRequest.DequeueJob): List<StartedJob> {
        log.debug("Dequeue job")
        val result = jobRequestRepository.dequeue()

        result.forEach {
            notifyDomain(
                JobStartedNotification(
                    shard = useCase.shard,
                    startedJob = it
                )
            )
        }
        return result
    }
}