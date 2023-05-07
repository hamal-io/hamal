package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.logger
import io.hamal.backend.notification.JobPlannedNotification
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.port.GenerateDomainIdPort

class PlanJobRequestHandler(
    val generateDomainId: GenerateDomainIdPort,
    val jobRepository: JobRepository,
    val notifyDomain: NotifyDomainPort
) : RequestOneUseCaseHandler<PlannedJob, JobRequest.PlanJob>(JobRequest.PlanJob::class) {
    val log = logger(PlanJobRequestHandler::class)
    override fun invoke(useCase: JobRequest.PlanJob): PlannedJob {
        log.debug("Create a new job for ${useCase.jobDefinition}")

        val result = jobRepository.planJob(
            JobRepository.JobToPlan(
                shard = useCase.shard,
                requestId = useCase.requestId,
                id = generateDomainId(useCase.shard, ::JobId),
                definition = useCase.jobDefinition,
                trigger = useCase.trigger
            )
        )

        notifyDomain(
            JobPlannedNotification(
                shard = result.shard,
                plannedJob = result
            )
        )

        return result
    }

}