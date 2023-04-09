package io.hamal.module.launchpad.application.job

import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.lib.domain_notification.notification.JobDefinitionDomainNotification.Created
import io.hamal.module.launchpad.core.job.model.JobDefinition
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort.JobDefinitionToCreate

data class CreateJobDefinitionUseCase(
    val regionId: RegionId,
    val reference: JobReference
) : CommandUseCase {

    class Operation(
        val create: CreateJobDefinitionPort,
        val notifyDomain: NotifyDomainPort
    ) : CommandUseCaseOperation<JobDefinition, CreateJobDefinitionUseCase>(
        JobDefinition::class,
        CreateJobDefinitionUseCase::class
    ) {
        override fun invoke(useCase: CreateJobDefinitionUseCase): List<JobDefinition> {
            val result = create(
                JobDefinitionToCreate(
                    regionId = useCase.regionId,
                    reference = useCase.reference
                )
            )
            notifyDomain(Created(result.id, result.regionId))
            return listOf(result)
        }
    }
}