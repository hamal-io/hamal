package io.hamal.api.req.handler.topic

import io.hamal.api.req.ReqHandler
import io.hamal.api.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.submitted_req.SubmittedCreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventBrokerRepository: BrokerRepository
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