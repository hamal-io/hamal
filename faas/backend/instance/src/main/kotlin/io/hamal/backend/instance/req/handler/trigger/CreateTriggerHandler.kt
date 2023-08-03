package io.hamal.backend.instance.req.handler.trigger

import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.event.TriggerCreatedEvent
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import org.springframework.stereotype.Component

@Component
class CreateTriggerHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: LogBrokerRepository<*>
) : ReqHandler<SubmittedCreateTriggerReq>(SubmittedCreateTriggerReq::class) {
    override fun invoke(req: SubmittedCreateTriggerReq) {
        funcQueryRepository.get(req.funcId)

        val trigger = when (req.type) {
            TriggerType.FixedRate -> triggerCmdRepository.create(
                TriggerCmdRepository.CreateFixedRateCmd(
                    id = req.cmdId(),
                    triggerId = req.triggerId,
                    name = req.triggerName,
                    correlationId = req.correlationId,
                    funcId = req.funcId,
                    inputs = req.inputs,
                    duration = requireNotNull(req.duration) { "duration must not be null" }
                )
            )

            TriggerType.Event -> {
                eventBrokerRepository.getTopic(req.topicId!!)

                triggerCmdRepository.create(
                    TriggerCmdRepository.CreateEventCmd(
                        id = req.cmdId(),
                        triggerId = req.triggerId,
                        name = req.triggerName,
                        correlationId = req.correlationId,
                        funcId = req.funcId,
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


