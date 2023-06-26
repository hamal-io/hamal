package io.hamal.backend.instance.req.handler.trigger

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.TriggerCmdService
import io.hamal.lib.domain.req.SubmittedCreateFixedRateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import org.springframework.stereotype.Component

@Component
class CreateTriggerHandler(
    private val triggerCmdService: TriggerCmdService
) : ReqHandler<SubmittedCreateTriggerReq>(SubmittedCreateTriggerReq::class) {
    override fun invoke(req: SubmittedCreateTriggerReq) {
        when (req) {
            is SubmittedCreateFixedRateTriggerReq -> triggerCmdService.create(
                req.cmdId(),
                TriggerCmdService.FixedRateTriggerToCreate(
                    id = req.triggerId,
                    name = req.triggerName,
                    funcId = req.funcId,
                    inputs = req.inputs,
                    secrets = req.secrets,
                    duration = req.duration
                )
            )
        }
    }
}