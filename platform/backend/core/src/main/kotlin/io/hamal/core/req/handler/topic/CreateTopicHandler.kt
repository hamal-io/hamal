package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.submitted_req.TopicCreateSubmitted
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicCreateSubmitted>(TopicCreateSubmitted::class) {
    override fun invoke(req: TopicCreateSubmitted) {
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