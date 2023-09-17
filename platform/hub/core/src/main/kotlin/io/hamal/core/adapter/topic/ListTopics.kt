package io.hamal.core.adapter.topic

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.Topic
import org.springframework.stereotype.Component

@Component
class ListTopics(private val eventBrokerRepository: BrokerRepository) {
    operator fun <T : Any> invoke(
        query: TopicQuery,
        responseHandler: (List<Topic>) -> T
    ): T {
        return responseHandler(eventBrokerRepository.listTopics(query))
    }
}