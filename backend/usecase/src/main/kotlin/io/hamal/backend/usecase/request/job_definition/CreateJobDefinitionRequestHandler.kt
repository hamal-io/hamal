package io.hamal.backend.usecase.request.job_definition

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.notification.JobDefinitionCreatedNotification
import io.hamal.backend.notification.ManualTriggerCreatedNotification
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.createJobDefinition
import io.hamal.backend.repository.api.createManualTrigger
import io.hamal.backend.usecase.request.JobDefinitionRequest
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.JobReference
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.port.GenerateDomainIdPort

class CreateJobDefinitionRequestHandler(
    private val notifyDomain: NotifyDomainPort,
    private val generateDomainId: GenerateDomainIdPort,
    private val jobDefinitionRepository: JobDefinitionRepository
) : RequestOneUseCaseHandler<JobDefinition, JobDefinitionRequest.JobDefinitionCreation>(JobDefinitionRequest.JobDefinitionCreation::class) {

    override fun invoke(useCase: JobDefinitionRequest.JobDefinitionCreation): JobDefinition {
        val result = jobDefinitionRepository.request(useCase.requestId) {
            val jobDefinitionId = createJobDefinition {
                reference = JobReference("jobRef")
            }
            createManualTrigger(jobDefinitionId) {
                reference = TriggerReference("manual")
            }
        }.first()

        notifyDomain(result)

        return result
    }

    private fun notifyDomain(jobDefinition: JobDefinition) {
        notifyDomain(
            JobDefinitionCreatedNotification(
                shard = jobDefinition.shard,
                id = jobDefinition.id
            )
        )

        jobDefinition.triggers.forEach { trigger ->
            notifyDomain.invoke(
                ManualTriggerCreatedNotification(
                    shard = jobDefinition.shard,
                    id = trigger.id
                )
            )
        }
    }

}