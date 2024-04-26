package io.hamal.core.request.handler.trigger

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.event.TriggerCreatedEvent
import org.springframework.stereotype.Component

@Component
class TriggerCreateHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val funcQueryRepository: FuncQueryRepository,
    private val topicRepository: TopicRepository
) : RequestHandler<TriggerCreateRequested>(TriggerCreateRequested::class) {

    override fun invoke(req: TriggerCreateRequested) {
        val func = funcQueryRepository.get(req.funcId)

        val trigger = when (req.type) {
            FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.id,
                    workspaceId = func.workspaceId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    namespaceId = req.namespaceId,
                    inputs = req.inputs,
                    duration = requireNotNull(req.duration) { "duration must not be null" }
                )
            )

            Event -> {
                val topic = topicRepository.get(req.topicId!!)

                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateEventCmd(
                        id = req.cmdId(),
                        triggerId = req.id,
                        workspaceId = func.workspaceId,
                        name = req.name,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
                        namespaceId = req.namespaceId,
                        inputs = req.inputs,
                        topicId = topic.id,
                    )
                )
            }

            Hook -> {
                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateHookCmd(
                        id = req.cmdId(),
                        triggerId = req.id,
                        workspaceId = func.workspaceId,
                        name = req.name,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
                        namespaceId = req.namespaceId,
                        inputs = req.inputs,
                    )
                )
            }

            Cron -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateCronCmd(
                    id = req.cmdId(),
                    triggerId = req.id,
                    workspaceId = func.workspaceId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    namespaceId = req.namespaceId,
                    inputs = req.inputs,
                    cron = requireNotNull(req.cron) { "cron must not be null" }
                )
            )

            Endpoint -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateEndpointCmd(
                    id = req.cmdId(),
                    triggerId = req.id,
                    workspaceId = func.workspaceId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    namespaceId = req.namespaceId,
                    inputs = req.inputs
                )
            )
        }

        emitEvent(req.cmdId(), trigger)
    }

    private fun emitEvent(cmdId: CmdId, trigger: Trigger) {
        eventEmitter.emit(cmdId, TriggerCreatedEvent(trigger))
    }
}


