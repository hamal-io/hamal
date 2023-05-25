package io.hamal.backend.cmd.handler.trigger

import io.hamal.backend.cmd.TriggerCmd.TriggerCreation
import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.createScheduleTrigger
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CreateTriggerRequestHandler(
    internal val eventEmitter: EventEmitter,
    internal val triggerRepository: TriggerCmdRepository
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
    eventEmitter.emit(
        TriggerCreatedEvent(
            shard = trigger.shard,
            id = trigger.id
        )
    )
}
