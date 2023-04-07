package io.hamal.module.launchpad.application.job

import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.Region
import io.hamal.lib.domain_notification.JobDefinitionDomainNotification
import io.hamal.lib.domain_notification.JobDefinitionDomainNotification.Created
import io.hamal.module.launchpad.core.job.model.JobDefinition
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort.JobDefinitionToCreate

data class CreateJobDefinitionUseCase(
    val region: Region,
    val reference: JobReference
) : CommandUseCase {

    class Operation(
        val create: CreateJobDefinitionPort,
        val notifyDomain: NotifyDomainPort<JobDefinitionDomainNotification>
    ) : CommandUseCaseOperation<JobDefinition, CreateJobDefinitionUseCase>(
        JobDefinition::class, CreateJobDefinitionUseCase::class
    ) {
        override fun invoke(useCase: CreateJobDefinitionUseCase): List<JobDefinition> {
            val result = create(
                JobDefinitionToCreate(
                    region = useCase.region,
                    reference = useCase.reference
                )
            )
            notifyDomain(Created(result.id))
            return listOf(result)
        }
    }
}