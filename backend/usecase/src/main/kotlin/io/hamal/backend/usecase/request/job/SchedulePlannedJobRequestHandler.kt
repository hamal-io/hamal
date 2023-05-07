package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.ScheduledJob
import io.hamal.backend.core.logger
import io.hamal.backend.notification.JobScheduledNotification
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.usecase.request.JobRequest.SchedulePlannedJob
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler

class SchedulePlannedJobRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val jobRepository: JobRepository
) : RequestOneUseCaseHandler<ScheduledJob, SchedulePlannedJob>(SchedulePlannedJob::class) {
    val log = logger(PlanJobRequestHandler::class)

    override fun invoke(useCase: SchedulePlannedJob): ScheduledJob {
        log.debug("Schedule job: ${useCase.plannedJob}")

        val result = jobRepository.schedule(useCase.plannedJob)
        notifyDomain(
            JobScheduledNotification(
                useCase.shard,
                result
            )
        )
        return result
    }

}