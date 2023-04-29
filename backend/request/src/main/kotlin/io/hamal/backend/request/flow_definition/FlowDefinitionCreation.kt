package io.hamal.backend.request.flow_definition

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.FlowDefinitionDomainNotification
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.store.api.FlowDefinitionStore
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.RegionId
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.port.GenerateDomainIdPort

sealed class FlowDefinitionRequest {


    data class CreateFlowDefinition(
        val regionId: RegionId,
    ) : ExecuteOneUseCase<FlowDefinition> {

        class Operation(
            val notifyDomain: NotifyDomainPort,
            val generateDomainId: GenerateDomainIdPort,
            val flowDefinitionStore: FlowDefinitionStore
        ) : ExecuteOneUseCaseOperation<FlowDefinition, CreateFlowDefinition>(CreateFlowDefinition::class) {

            override fun invoke(useCase: CreateFlowDefinition): FlowDefinition {
                val resultId = generateDomainId(useCase.regionId, ::FlowDefinitionId)

                // fixme should create trigger in separate use case
                val result = FlowDefinition(
                    id = resultId,
                    triggers = listOf(
                        Trigger.ManualTrigger(
                            id = generateDomainId(useCase.regionId, ::TriggerId),
                            reference = TriggerReference("1234"),
                            flowDefinitionId = resultId
                        ),
                        Trigger.ManualTrigger(
                            id = generateDomainId(useCase.regionId, ::TriggerId),
                            reference = TriggerReference("345"),
                            flowDefinitionId = resultId
                        ),
                        Trigger.ManualTrigger(
                            id = generateDomainId(useCase.regionId, ::TriggerId),
                            reference = TriggerReference("345"),
                            flowDefinitionId = resultId
                        ),
                        Trigger.ManualTrigger(
                            id = generateDomainId(useCase.regionId, ::TriggerId),
                            reference = TriggerReference("345"),
                            flowDefinitionId = resultId
                        )
                    )
                )

                flowDefinitionStore.create(result)

                // add trigger use case
                // can be atomic
                //flowDefinitionStore.update(flowDefinitionId){ definition ->
                //      triggers = triggers.plus(newTrigger)
                //}
                //  notify new trigger was created
                // notify  flow definition updated

                notifyDomain.invoke(
                    FlowDefinitionDomainNotification.Created(
                        flowDefinition = result,
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
}