package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogTopicAppenderImpl
import org.springframework.stereotype.Component

@Component
class AppendToHandler(
    private val topicRepository: TopicRepository,
    logBrokerRepository: LogBrokerRepository
) : RequestHandler<TopicAppendEventRequested>(TopicAppendEventRequested::class) {

    override fun invoke(req: TopicAppendEventRequested) {
        val topic = topicRepository.get(req.topicId)
        appender.append(req.cmdId(), topic.logTopicId, req.payload)
    }

    private val appender = LogTopicAppenderImpl<TopicEventPayload>(logBrokerRepository)
}