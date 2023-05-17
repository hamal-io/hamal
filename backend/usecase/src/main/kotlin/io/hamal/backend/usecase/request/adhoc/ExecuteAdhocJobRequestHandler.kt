package io.hamal.backend.usecase.request.adhoc

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.notification.AdhocTriggerInvokedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.createJobDefinition
import io.hamal.backend.repository.api.createManualTrigger
import io.hamal.backend.repository.api.createScriptTask
import io.hamal.backend.usecase.request.AdhocRequest.ExecuteJAdhocJob
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.*

class ExecuteAdhocJobRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val jobDefinitionRepository: JobDefinitionRepository
) : RequestOneUseCaseHandler<JobDefinition, ExecuteJAdhocJob>(ExecuteJAdhocJob::class) {
    override fun invoke(useCase: ExecuteJAdhocJob): JobDefinition {
        //FIXME just a quick hack - job definition is not really required ?!
        val result = createJobDefinition(useCase)
        notifyDomain(
            AdhocTriggerInvokedNotification(
                shard = useCase.shard,
                invokedTrigger = InvokedTrigger.Adhoc(
                    id = InvokedTriggerId(0),
                    jobDefinition = result,
                    trigger = Trigger.AdhocTrigger(
                        id = TriggerId(1),
                        reference = TriggerReference("adhoc"),
                        jobDefinitionId = result.id
                    ),
                    invokedAt = InvokedAt.now(),
                    invokedBy = Requester.tenant(TenantId(12))
                )
            )
        )
        return result
    }
}

internal fun ExecuteAdhocJobRequestHandler.createJobDefinition(useCase: ExecuteJAdhocJob): JobDefinition {
    return jobDefinitionRepository.request(useCase.requestId) {
        val jobDefinitionId = createJobDefinition {
            reference = JobReference("jobRef")
        }
        createScriptTask(jobDefinitionId) {
            code = HamalScriptCode(useCase.script)
        }
        createManualTrigger(jobDefinitionId) {
            reference = TriggerReference("manual")
        }
    }.first()
}

