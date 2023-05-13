package io.hamal.backend.usecase.request.job

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobPlannedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.request.JobRequest.PlanJob
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort

class PlanJobRequestHandler(
    val generateDomainId: GenerateDomainIdPort,
    val jobRequestRepository: JobRequestRepository,
    val notifyDomain: NotifyDomainPort
) : RequestOneUseCaseHandler<PlannedJob, PlanJob>(PlanJob::class) {
    val log = logger(PlanJobRequestHandler::class)
    override fun invoke(useCase: PlanJob): PlannedJob {
        log.debug("Create a new job for ${useCase.jobDefinition}")

        val result = jobRequestRepository.planJob(
            useCase.requestId,
            JobRequestRepository.JobToPlan(
                shard = useCase.shard,
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