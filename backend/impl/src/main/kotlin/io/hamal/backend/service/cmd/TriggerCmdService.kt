package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.createFixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ComputeId
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
    fun create(computeId: ComputeId, triggerToCreate: TriggerToCreate): Trigger =
        createTrigger(computeId, triggerToCreate).also { emitEvent(computeId, it) }

    data class TriggerToCreate(
        val shard: Shard,
        val type: TriggerType,
        val name: TriggerName,
        val funcId: FuncId,
        val duration: Duration?
    )
}

private fun TriggerCmdService.createTrigger(computeId: ComputeId, triggerToCreate: TriggerCmdService.TriggerToCreate): Trigger {
    return triggerCmdRepository.request(computeId) {
        createFixedRateTrigger {
            name = triggerToCreate.name
            funcId = triggerToCreate.funcId
            duration = triggerToCreate.duration!! //FIXME
        }
    }.first()
}

private fun TriggerCmdService.emitEvent(computeId: ComputeId, trigger: Trigger) {
    eventEmitter.emit(
        computeId, TriggerCreatedEvent(
//            computeId = ComputeId(123), //FIXME
            shard = trigger.shard,
            trigger
        )
    )
}
