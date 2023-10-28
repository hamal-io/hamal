package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.submitted_req.TopicCreateSubmittedReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicCreateSubmittedReq>(TopicCreateSubmittedReq::class) {
    override fun invoke(req: TopicCreateSubmittedReq) {
        eventBrokerRepository.create(
            req.cmdId(),
            TopicToCreate(
                id = req.id,
                name = req.name,
                namespaceId = req.namespaceId,
                groupId = req.groupId
            )
        )
    }
}