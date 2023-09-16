package io.hamal.core.component.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import org.springframework.stereotype.Component

@Component
class GetTopic(private val eventBrokerRepository: BrokerRepository) {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        responseHandler: (Topic) -> T
    ): T {
        return responseHandler(eventBrokerRepository.getTopic(topicId))
    }
}