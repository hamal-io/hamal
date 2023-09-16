package io.hamal.core.component.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicEntry
import org.springframework.stereotype.Component

@Component
class ListTopicEntries(private val eventBrokerRepository: BrokerRepository) {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, Topic) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(topicId)
        return responseHandler(eventBrokerRepository.listEntries(topic, query), topic)
    }
}