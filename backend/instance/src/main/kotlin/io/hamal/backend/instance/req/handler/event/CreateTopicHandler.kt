package io.hamal.backend.instance.req.handler.event

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.EventCmdService
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventCmdService: EventCmdService<*>
) : ReqHandler<SubmittedCreateTopicReq>(SubmittedCreateTopicReq::class) {
    override fun invoke(req: SubmittedCreateTopicReq) {
        eventCmdService.create(
            req.cmdId(),
            EventCmdService.TopicToCreate(
                id = req.topicId,
                name = req.name
            )
        )
    }
}