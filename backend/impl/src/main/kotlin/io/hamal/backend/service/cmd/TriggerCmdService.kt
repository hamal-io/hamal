package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.createFixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
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
    fun create(cmdId: CmdId, triggerToCreate: TriggerToCreate): Trigger =
        createTrigger(cmdId, triggerToCreate).also { emitEvent(cmdId, it) }

    data class TriggerToCreate(
        val shard: Shard,
        val type: TriggerType,
        val name: TriggerName,
        val funcId: FuncId,
        val duration: Duration?
    )
}

private fun TriggerCmdService.createTrigger(cmdId: CmdId, triggerToCreate: TriggerCmdService.TriggerToCreate): Trigger {
    return triggerCmdRepository.request(cmdId) {
        createFixedRateTrigger {
            name = triggerToCreate.name
            funcId = triggerToCreate.funcId
            duration = triggerToCreate.duration!! //FIXME
        }
    }.first()
}

private fun TriggerCmdService.emitEvent(cmdId: CmdId, trigger: Trigger) {
    eventEmitter.emit(
        cmdId, TriggerCreatedEvent(
//            cmdId = CommandId(123), //FIXME
            shard = trigger.shard,
            trigger
        )
    )
}
