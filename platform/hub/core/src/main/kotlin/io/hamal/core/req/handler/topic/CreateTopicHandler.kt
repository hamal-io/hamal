package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.submitted_req.SubmittedCreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<SubmittedCreateTopicReq>(SubmittedCreateTopicReq::class) {
    override fun invoke(req: SubmittedCreateTopicReq) {
        eventBrokerRepository.create(
            req.cmdId(),
            TopicToCreate(
                id = req.id,
                name = req.name,
                groupId = req.groupId
            )
        )
    }
}