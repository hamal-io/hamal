package io.hamal.backend.usecase.request.trigger

import io.hamal.backend.core.trigger.InvokedTrigger.Manual
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.notification.ManualTriggerInvokedNotification
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.usecase.request.TriggerRequest
import io.hamal.lib.Requester
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.InvokedAt
import io.hamal.lib.vo.InvokedTriggerId
import io.hamal.lib.vo.TenantId

class ManualTriggerInvocationRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val jobDefinitionRepository: JobDefinitionRepository
) : RequestOneUseCaseHandler<Manual, TriggerRequest.ManualTriggerInvocation>(
    TriggerRequest.ManualTriggerInvocation::class
) {

    override fun invoke(useCase: TriggerRequest.ManualTriggerInvocation): Manual {
        val trigger = jobDefinitionRepository.getTrigger(useCase.triggerId)
        check(trigger is Trigger.ManualTrigger) { "Trigger with id ${trigger.id} can not be triggered manually" }

        val result = Manual(
            id = InvokedTriggerId(123),
            trigger = trigger,
            invokedAt = InvokedAt.now(),
            invokedBy = Requester.tenant(TenantId(12))
        )

        notifyDomain(
            ManualTriggerInvokedNotification(
                shard = useCase.shard,
                invokedTrigger = result
            )
        )

        return result
    }

}