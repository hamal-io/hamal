package io.hamal.backend.req.handler.event

import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.backend.service.cmd.EventCmdService.EventToAppend
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AppendEventHandler(
    @Autowired private val eventCmdService: EventCmdService<*>
) : ReqHandler<SubmittedAppendEventReq>(SubmittedAppendEventReq::class) {
    override fun invoke(req: SubmittedAppendEventReq) {
        eventCmdService.append(
            req.cmdId(),
            EventToAppend(
                topicId = req.topicId,
                contentTpe = req.contentType,
                content = req.bytes
            )
        )
    }
}