package io.hamal.core.req.handler.trigger

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod.Post
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.vo.FlowName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.TriggerCreatedEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.submitted_req.TriggerCreateSubmitted
import org.springframework.stereotype.Component

@Component
class CreateTriggerHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: PlatformEventEmitter,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val flowQueryRepository: FlowQueryRepository,
    private val hookQueryRepository: HookQueryRepository
) : ReqHandler<TriggerCreateSubmitted>(TriggerCreateSubmitted::class) {

    override fun invoke(req: TriggerCreateSubmitted) {
        val func = funcQueryRepository.get(req.funcId)

        val trigger = when (req.type) {
            FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.triggerId,
                    groupId = func.groupId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    flowId = req.flowId ?: flowQueryRepository.get(FlowName("hamal")).id,
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
                        flowId = req.flowId ?: flowQueryRepository.get(FlowName("hamal")).id,
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
                        flowId = req.flowId ?: flowQueryRepository.get(FlowName("hamal")).id,
                        inputs = req.inputs,
                        hookId = hook.id,
                        hookMethods = req.hookMethods ?: setOf(Post)
                    )
                )
            }
        }

        emitEvent(req.cmdId(), trigger)
    }

    private fun emitEvent(cmdId: CmdId, trigger: Trigger) {
        eventEmitter.emit(cmdId, TriggerCreatedEvent(trigger))
    }
}


