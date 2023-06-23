package io.hamal.backend.req.handler.event

import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    @Autowired private val eventCmdService: EventCmdService<*>
) : ReqHandler<SubmittedCreateTopicReq>(SubmittedCreateTopicReq::class){
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