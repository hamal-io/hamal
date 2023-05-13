package io.hamal.backend.usecase.request.job_definition

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.notification.JobDefinitionCreatedNotification
import io.hamal.backend.core.notification.ManualTriggerCreatedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.createJobDefinition
import io.hamal.backend.repository.api.createManualTrigger
import io.hamal.backend.repository.api.createScriptTask
import io.hamal.backend.usecase.request.JobDefinitionRequest.JobDefinitionCreation
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.HamalScriptCode
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.TriggerReference

class CreateJobDefinitionRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val jobDefinitionRepository: JobDefinitionRepository
) : RequestOneUseCaseHandler<JobDefinition, JobDefinitionCreation>(JobDefinitionCreation::class) {
    override fun invoke(useCase: JobDefinitionCreation): JobDefinition {
        val result = createJobDefinition(useCase)
        notifyDefinitionCreated(result)
        notifyTriggersCreated(result)
        return result
    }
}

internal fun CreateJobDefinitionRequestHandler.createJobDefinition(useCase: JobDefinitionCreation): JobDefinition {
    return jobDefinitionRepository.request(useCase.requestId) {
        val jobDefinitionId = createJobDefinition {
            reference = JobReference("jobRef")
        }
        createScriptTask(jobDefinitionId) {
            code = HamalScriptCode(
                """
                |assert( 1<1 )
                |42
            """.trimMargin()
            )
        }
        createManualTrigger(jobDefinitionId) {
            reference = TriggerReference("manual")
        }
    }.first()
}

internal fun CreateJobDefinitionRequestHandler.notifyDefinitionCreated(jobDefinition: JobDefinition) {
    notifyDomain(
        JobDefinitionCreatedNotification(
            shard = jobDefinition.shard,
            id = jobDefinition.id
        )
    )
}

internal fun CreateJobDefinitionRequestHandler.notifyTriggersCreated(jobDefinition: JobDefinition) {
    jobDefinition.triggers.forEach { trigger ->
        notifyDomain.invoke(
            ManualTriggerCreatedNotification(
                shard = jobDefinition.shard,
                id = trigger.id
            )
        )
    }
}