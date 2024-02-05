package io.hamal.core.request.handler.trigger

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod.Post
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.event.TriggerCreatedEvent
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.stereotype.Component

@Component
class TriggerCreateHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val hookQueryRepository: HookQueryRepository
) : io.hamal.core.request.RequestHandler<TriggerCreateRequested>(TriggerCreateRequested::class) {

    override fun invoke(req: TriggerCreateRequested) {
        val func = funcQueryRepository.get(req.funcId)

        val trigger = when (req.triggerType) {
            FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.triggerId,
                    groupId = func.groupId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    flowId = req.flowId,
                    inputs = req.inputs,
                    duration = requireNotNull(req.duration) { "duration must not be null" }
                )
            )

            Event -> {
                val topic = eventBrokerRepository.getTopic(req.topicId!!)

                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateEventCmd(
                        id = req.cmdId(),
                        triggerId = req.triggerId,
                        groupId = func.groupId,
                        name = req.name,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
                        flowId = req.flowId,
                        inputs = req.inputs,
                        topicId = topic.id,
                    )
                )
            }

            Hook -> {
                val hook = hookQueryRepository.get(req.hookId!!)
                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateHookCmd(
                        id = req.cmdId(),
                        triggerId = req.triggerId,
                        groupId = func.groupId,
                        name = req.name,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
                        flowId = req.flowId,
                        inputs = req.inputs,
                        hookId = hook.id,
                        hookMethod = req.hookMethod ?: Post
                    )
                )


            }

            Cron -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateCronCmd(
                    id = req.cmdId(),
                    triggerId = req.triggerId,
                    groupId = func.groupId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    flowId = req.flowId,
                    inputs = req.inputs,
                    cron = requireNotNull(req.cron) { "cron expression must not be null" }
                )
            )

        }

        emitEvent(req.cmdId(), trigger)
    }

    private fun emitEvent(cmdId: CmdId, trigger: Trigger) {
        eventEmitter.emit(cmdId, TriggerCreatedEvent(trigger))
    }
}


