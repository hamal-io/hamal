package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.CompletedJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobCompletedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.request.JobRequest.CompleteStartedJob
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CompleteStartedJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRequestRepository: JobRequestRepository
) : RequestOneUseCaseHandler<CompletedJob, CompleteStartedJob>(CompleteStartedJob::class) {
    private val log = logger(CompleteStartedJobRequestHandler::class)
    override fun invoke(useCase: CompleteStartedJob): CompletedJob {
        log.debug("Complete started job: ${useCase.startedJob}")
        val result = jobRequestRepository.complete(useCase.requestId, useCase.startedJob)
        notifyDomain(
            JobCompletedNotification(
                useCase.shard,
                result
            )
        )
        return result
    }

}