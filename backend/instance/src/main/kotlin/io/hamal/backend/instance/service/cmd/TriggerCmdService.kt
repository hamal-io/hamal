package io.hamal.backend.instance.service.cmd

import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.event.TriggerCreatedEvent
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class TriggerCmdService(
    val triggerCmdRepository: TriggerCmdRepository,
    val eventEmitter: SystemEventEmitter<*>
) {
    fun create(cmdId: CmdId, triggerToCreate: TriggerToCreate): Trigger =
        createTrigger(cmdId, triggerToCreate).also { emitEvent(cmdId, it) }

    data class TriggerToCreate(
        val type: TriggerType,
        val id: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val inputs: TriggerInputs,
        val secrets: TriggerSecrets,
        val duration: Duration?,
        val topicId: TopicId?
    )
}

private fun TriggerCmdService.createTrigger(cmdId: CmdId, triggerToCreate: TriggerCmdService.TriggerToCreate): Trigger {
    return when (triggerToCreate.type) {
        TriggerType.FixedRate -> triggerCmdRepository.create(
            TriggerCmdRepository.CreateFixedRateCmd(
                id = cmdId,
                triggerId = triggerToCreate.id,
                name = triggerToCreate.name,
                funcId = triggerToCreate.funcId,
                inputs = triggerToCreate.inputs,
                secrets = triggerToCreate.secrets,
                duration = triggerToCreate.duration!!
            )
        )

        TriggerType.Event -> triggerCmdRepository.create(
            TriggerCmdRepository.CreateEventCmd(
                id = cmdId,
                triggerId = triggerToCreate.id,
                name = triggerToCreate.name,
                funcId = triggerToCreate.funcId,
                inputs = triggerToCreate.inputs,
                secrets = triggerToCreate.secrets,
                topicId = triggerToCreate.topicId!!
            )
        )
    }
}

private fun TriggerCmdService.emitEvent(cmdId: CmdId, trigger: Trigger) {
    eventEmitter.emit(cmdId, TriggerCreatedEvent(trigger))
}
