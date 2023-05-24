package io.hamal.backend.infra.usecase.request.trigger

import io.hamal.backend.core.notification.TriggerCreatedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.infra.usecase.request.TriggerRequest.TriggerCreation
import io.hamal.backend.repository.api.TriggerRequestRepository
import io.hamal.backend.repository.api.createScheduleTrigger
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CreateTriggerRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val triggerRepository: TriggerRequestRepository
) : RequestOneUseCaseHandler<Trigger, TriggerCreation>(TriggerCreation::class) {
    override fun invoke(useCase: TriggerCreation): Trigger {
        val result = createTrigger(useCase)
        notifyTriggerCreated(result)
//        notifyTriggersCreated(result)
        return result
    }
}

internal fun CreateTriggerRequestHandler.createTrigger(useCase: TriggerCreation): Trigger {
    return triggerRepository.request(useCase.reqId) {
        createScheduleTrigger {
            name = useCase.name
            code = useCase.code
        }
    }.first()
}

internal fun CreateTriggerRequestHandler.notifyTriggerCreated(trigger: Trigger) {
    notifyDomain(
        TriggerCreatedNotification(
            shard = trigger.shard,
            id = trigger.id
        )
    )
}
