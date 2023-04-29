package io.hamal.backend.usecase.flow_definition

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.FlowDefinitionDomainNotification
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.store.api.FlowDefinitionStore
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.port.GenerateDomainIdPort

class CreateFlowDefinitionRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val generateDomainId: GenerateDomainIdPort,
    val flowDefinitionStore: FlowDefinitionStore
) : ExecuteOneUseCaseOperation<FlowDefinition, FlowDefinitionRequest.FlowDefinitionCreation>(FlowDefinitionRequest.FlowDefinitionCreation::class) {

    override fun invoke(useCase: FlowDefinitionRequest.FlowDefinitionCreation): FlowDefinition {
        val resultId = generateDomainId(useCase.shard, ::FlowDefinitionId)


        //  val commands = store.newCommands()
        // val definitionId = commands.insertFlowDefinition(definiton props..)
        // val triggerOneId = commands.insertTrigger(definitionId, trigger props..)
        // store(commands)

        // fixme should create trigger in separate use case
        val result = FlowDefinition(
            id = resultId,
            triggers = listOf(
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("1234"),
                    flowDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("345"),
                    flowDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("345"),
                    flowDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
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

        notifyDomain(
            FlowDefinitionDomainNotification.Created(
                flowDefinition = result,
                shard = useCase.shard
            )
        )

        result.triggers.forEach { trigger ->
            notifyDomain.invoke(TriggerDomainNotification.Created(trigger, useCase.shard))
        }

        return result
    }

}