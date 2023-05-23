package io.hamal.backend.usecase.request.trigger

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.core.trigger.InvokedTrigger.Manual
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.usecase.request.TriggerRequest
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class ManualTriggerInvocationRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val funcRepository: FuncRequestRepository
) : RequestOneUseCaseHandler<Manual, TriggerRequest.ManualTriggerInvocation>(
    TriggerRequest.ManualTriggerInvocation::class
) {

    override fun invoke(useCase: TriggerRequest.ManualTriggerInvocation): Manual {
//        val trigger = funcRepository.getTrigger(useCase.triggerId)
//        check(trigger is Trigger.ManualTrigger) { "Trigger with id ${trigger.id} can not be triggered manually" }
//
//        val result = Manual(
//            id = InvokedTriggerId(123),
//            trigger = trigger,
//            invokedAt = InvokedAt.now(),
//            invokedBy = Requester.tenant(TenantId(12))
//        )
//
//        notifyDomain(
//            ManualTriggerInvokedNotification(
//                shard = useCase.shard,
//                invokedTrigger = result
//            )
//        )
//
//        return result
        TODO("remove manual trigger entirely")
    }

}