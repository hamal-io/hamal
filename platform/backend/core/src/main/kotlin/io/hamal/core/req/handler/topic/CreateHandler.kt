package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.lib.domain.request.TopicCreateRequested
import org.springframework.stereotype.Component

@Component
class TopicCreateHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicCreateRequested>(TopicCreateRequested::class) {
    override fun invoke(req: TopicCreateRequested) {
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