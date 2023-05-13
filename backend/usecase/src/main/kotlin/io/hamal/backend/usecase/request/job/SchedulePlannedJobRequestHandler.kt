package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.ScheduledJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobScheduledNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.request.JobRequest.SchedulePlannedJob
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class SchedulePlannedJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRequestRepository: JobRequestRepository
) : RequestOneUseCaseHandler<ScheduledJob, SchedulePlannedJob>(SchedulePlannedJob::class) {
    val log = logger(PlanJobRequestHandler::class)

    override fun invoke(useCase: SchedulePlannedJob): ScheduledJob {
        log.debug("Schedule job: ${useCase.plannedJob}")

        val result = jobRequestRepository.schedule(useCase.requestId, useCase.plannedJob)
        notifyDomain(
            JobScheduledNotification(
                useCase.shard,
                result
            )
        )
        return result
    }

}