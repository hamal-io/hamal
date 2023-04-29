package io.hamal.backend.application.job

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.JobDefinitionDomainNotification
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.RegionId
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.port.GenerateDomainIdPort

data class CreateJobDefinitionUseCase(
    val regionId: RegionId
) : ExecuteOneUseCase<JobDefinition> {
    class Operation(
        val notifyDomain: NotifyDomainPort,
        val generateDomainId: GenerateDomainIdPort
    ) : ExecuteOneUseCaseOperation<JobDefinition, CreateJobDefinitionUseCase>(CreateJobDefinitionUseCase::class) {
        override fun invoke(useCase: CreateJobDefinitionUseCase): JobDefinition {
            val resultId = generateDomainId(useCase.regionId, ::JobDefinitionId)
            val result = JobDefinition(
                id = resultId,
                triggers = listOf(
                    Trigger.ManualTrigger(
                        id = generateDomainId(useCase.regionId, ::TriggerId),
                        reference = TriggerReference("1234"),
                        jobDefinitionId = resultId
                    ),
                    Trigger.ManualTrigger(
                        id = generateDomainId(useCase.regionId, ::TriggerId),
                        reference = TriggerReference("345"),
                        jobDefinitionId = resultId
                    ),
                    Trigger.ManualTrigger(
                        id = generateDomainId(useCase.regionId, ::TriggerId),
                        reference = TriggerReference("345"),
                        jobDefinitionId = resultId
                    ),
                    Trigger.ManualTrigger(
                        id = generateDomainId(useCase.regionId, ::TriggerId),
                        reference = TriggerReference("345"),
                        jobDefinitionId = resultId
                    )
                )
            )
            notifyDomain.invoke(
                JobDefinitionDomainNotification.Created(
                    jobDefinition = result,
                    regionId = useCase.regionId
                )
            )

            result.triggers.forEach { trigger ->
                notifyDomain.invoke(TriggerDomainNotification.Created(trigger, useCase.regionId))
            }

            return result
        }
    }
}
