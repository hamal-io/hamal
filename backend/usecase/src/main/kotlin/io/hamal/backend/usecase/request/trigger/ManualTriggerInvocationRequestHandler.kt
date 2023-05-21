package io.hamal.backend.usecase.request.trigger

import io.hamal.backend.core.trigger.Cause.Manual
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.core.notification.ManualTriggerInvokedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.usecase.request.TriggerRequest
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.InvokedAt
import io.hamal.lib.domain.vo.CauseId
import io.hamal.lib.domain.vo.TenantId

class ManualTriggerInvocationRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val funcRepository: FuncRepository
) : RequestOneUseCaseHandler<Manual, TriggerRequest.ManualTriggerInvocation>(
    TriggerRequest.ManualTriggerInvocation::class
) {

    override fun invoke(useCase: TriggerRequest.ManualTriggerInvocation): Manual {
        val trigger = funcRepository.getTrigger(useCase.triggerId)
        check(trigger is Trigger.ManualTrigger) { "Trigger with id ${trigger.id} can not be triggered manually" }

        val result = Manual(
            id = CauseId(123),
            func = funcRepository.get(trigger.funcId),
            trigger = trigger,
            invokedAt = InvokedAt.now(),
            invokedBy = Requester.tenant(TenantId(12))
        )

        notifyDomain(
            ManualTriggerInvokedNotification(
                shard = useCase.shard,
                cause = result
            )
        )

        return result
    }

}