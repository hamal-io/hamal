package io.hamal.backend.instance.req.handler.state

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.lib.domain.req.SubmittedSetStateReq
import org.springframework.stereotype.Component

@Component
class SetStateHandler : ReqHandler<SubmittedSetStateReq>(SubmittedSetStateReq::class) {
    override fun invoke(req: SubmittedSetStateReq) {
        TODO()
    }
}