package io.hamal.backend.req.handler.trigger

import io.hamal.backend.event.HubEventEmitter
import io.hamal.backend.event.event.TriggerCreatedEvent
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.submitted_req.SubmittedCreateTriggerReq
import org.springframework.stereotype.Component

@Component
class CreateTriggerHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: HubEventEmitter,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : ReqHandler<SubmittedCreateTriggerReq>(SubmittedCreateTriggerReq::class) {
    override fun invoke(req: SubmittedCreateTriggerReq) {
        funcQueryRepository.get(req.funcId)

        val trigger = when (req.type) {
            TriggerType.FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.id,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
                    inputs = req.inputs,
                    duration = requireNotNull(req.duration) { "duration must not be null" }
                )
            )

            TriggerType.Event -> {
                eventBrokerRepository.getTopic(req.topicId!!)

                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateEventCmd(
                        id = req.cmdId(),
                        triggerId = req.id,
                        name = req.name,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
                        namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
                        inputs = req.inputs,
                        topicId = requireNotNull(req.topicId) { "topicId must not be null" },
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


