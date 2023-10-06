package io.hamal.core.req.handler.trigger

import io.hamal.core.event.HubEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.event.TriggerCreatedEvent
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
        val func = funcQueryRepository.get(req.funcId)

        val trigger = when (req.type) {
            FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.id,
                    groupId = func.groupId,
                    name = req.name,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
                    inputs = req.inputs,
                    duration = requireNotNull(req.duration) { "duration must not be null" }
                )
            )

            Event -> {
                eventBrokerRepository.getTopic(req.topicId!!)

                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateEventCmd(
                        id = req.cmdId(),
                        triggerId = req.id,
                        groupId = func.groupId,
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


