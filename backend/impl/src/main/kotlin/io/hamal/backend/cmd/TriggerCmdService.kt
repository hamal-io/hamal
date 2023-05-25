package io.hamal.backend.cmd

import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.createScheduleTrigger
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TriggerName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TriggerCmdService
@Autowired constructor(
    val triggerCmdRepository: TriggerCmdRepository,
    val eventEmitter: EventEmitter
) {
    fun create(triggerToCreate: TriggerToCreate): Trigger = createTrigger(triggerToCreate).also(this::emitEvent)

    data class TriggerToCreate(
        val reqId: ReqId,
        val shard: Shard,
        val name: TriggerName,
        val code: Code
    )
}

private fun TriggerCmdService.createTrigger(triggerToCreate: TriggerCmdService.TriggerToCreate): Trigger {
    return triggerCmdRepository.request(triggerToCreate.reqId) {
        createScheduleTrigger {
            name = triggerToCreate.name
            code = triggerToCreate.code
        }
    }.first()
}

private fun TriggerCmdService.emitEvent(trigger: Trigger) {
    eventEmitter.emit(
        TriggerCreatedEvent(
            shard = trigger.shard,
            id = trigger.id
        )
    )
}
