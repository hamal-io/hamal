package io.hamal.backend.usecase.job_definition

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.job_definition.JobDefinitionCreatedNotification
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.core.trigger.ManualTriggerCreatedNotification
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.port.GenerateDomainIdPort

class CreateJobDefinitionRequestHandler(
    val notifyDomain: NotifyDomainPort,
    val generateDomainId: GenerateDomainIdPort,
    val jobDefinitionStore: JobDefinitionRepository
) : RequestOneUseCaseHandler<JobDefinition, JobDefinitionRequest.JobDefinitionCreation>(JobDefinitionRequest.JobDefinitionCreation::class) {

    override fun invoke(useCase: JobDefinitionRequest.JobDefinitionCreation): JobDefinition {
        val resultId = generateDomainId(useCase.shard, ::JobDefinitionId)

        //  val commands = store.request(){
        //     val definitionId = commands.insertJobDefinition(definiton props..)
        //     val triggerOneId = commands.addTrigger(definitionId, trigger props..)
        //     commands.removeTrigger(definitionId, trigger props..)
        // }
        // store(commands)

        // store.request(){
        //      job.
        //}


        // fixme should create trigger in separate use case
        val result = JobDefinition(
            id = resultId,
            reference = JobReference("ABC"),
            triggers = listOf(
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("1234"),
                    jobDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("345"),
                    jobDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("345"),
                    jobDefinitionId = resultId
                ),
                Trigger.ManualTrigger(
                    id = generateDomainId(useCase.shard, ::TriggerId),
                    reference = TriggerReference("345"),
                    jobDefinitionId = resultId
                )
            )
        )

//        jobDefinitionStore.create(result)

        // add trigger use case
        // can be atomic
        //jobDefinitionStore.update(jobDefinitionId){ definition ->
        //      triggers = triggers.plus(newTrigger)
        //}
        //  notify new trigger was created
        // notify  job definition updated

        notifyDomain(
            JobDefinitionCreatedNotification(
                shard = useCase.shard,
                id = resultId
            )
        )

        result.triggers.forEach { trigger ->
            notifyDomain.invoke(
                ManualTriggerCreatedNotification(
                    shard = useCase.shard,
                    id = trigger.id
                )
            )
        }

        return result
    }

}