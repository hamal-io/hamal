package io.hamal.backend.instance.req.handler.topic

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.submitted_req.SubmittedCreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventBrokerRepository: LogBrokerRepository
) : ReqHandler<SubmittedCreateTopicReq>(SubmittedCreateTopicReq::class) {
    override fun invoke(req: SubmittedCreateTopicReq) {
        eventBrokerRepository.create(
            req.cmdId(),
            CreateTopic.TopicToCreate(
                id = req.id,
                name = req.name
            )
        )
    }
}