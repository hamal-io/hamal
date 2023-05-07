package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobPlannedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.usecase.request.JobRequest.PlanJob
import io.hamal.lib.core.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.core.vo.JobId
import io.hamal.lib.core.vo.port.GenerateDomainIdPort

class PlanJobRequestHandler(
    val generateDomainId: GenerateDomainIdPort,
    val jobRepository: JobRepository,
    val notifyDomain: NotifyDomainPort
) : RequestOneUseCaseHandler<PlannedJob, PlanJob>(PlanJob::class) {
    val log = logger(PlanJobRequestHandler::class)
    override fun invoke(useCase: PlanJob): PlannedJob {
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