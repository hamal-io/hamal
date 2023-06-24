package io.hamal.backend.instance.req.handler.event

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.EventCmdService
import io.hamal.backend.instance.service.cmd.EventCmdService.EventToAppend
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