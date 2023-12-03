package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.submitted_req.TopicCreateSubmitted
import org.springframework.stereotype.Component

@Component
class TopicCreateHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicCreateSubmitted>(TopicCreateSubmitted::class) {
    override fun invoke(req: TopicCreateSubmitted) {
        eventBrokerRepository.create(
            req.cmdId(),
            TopicToCreate(
                id = req.topicId,
                name = req.name,
                flowId = req.flowId,
                groupId = req.groupId
            )
        )
    }
}