package io.hamal.backend.service.cmd

import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.createFixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.time.Duration

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
        val type: TriggerType,
        val name: TriggerName,
        val funcId: FuncId,
        val duration: Duration?
    )
}

private fun TriggerCmdService.createTrigger(triggerToCreate: TriggerCmdService.TriggerToCreate): Trigger {
    return triggerCmdRepository.request(triggerToCreate.reqId) {
        createFixedRateTrigger {
            name = triggerToCreate.name
            funcId = triggerToCreate.funcId
            duration = triggerToCreate.duration!! //FIXME
        }
    }.first()
}

private fun TriggerCmdService.emitEvent(trigger: Trigger) {
    eventEmitter.emit(
        TriggerCreatedEvent(
            shard = trigger.shard,
            trigger
        )
    )
}
