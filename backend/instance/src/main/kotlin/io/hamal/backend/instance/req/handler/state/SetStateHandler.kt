package io.hamal.backend.instance.req.handler.state

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.service.cmd.TriggerCmdService
import io.hamal.lib.domain.req.SubmittedSetStateReq
import org.springframework.stereotype.Component

@Component
class SetStateHandler(
    private val state: TriggerCmdService
) : ReqHandler<SubmittedSetStateReq>(SubmittedSetStateReq::class) {
    override fun invoke(req: SubmittedSetStateReq) {
        TODO()
    }
}