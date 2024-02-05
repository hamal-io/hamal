package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicAppendToRequested
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogTopicAppenderImpl
import org.springframework.stereotype.Component

@Component
class AppendToHandler(
    private val topicRepository: TopicRepository,
    logBrokerRepository: LogBrokerRepository
) : RequestHandler<TopicAppendToRequested>(TopicAppendToRequested::class) {

    override fun invoke(req: TopicAppendToRequested) {
        val topic = topicRepository.get(req.topicId)
        appender.append(req.cmdId(), topic.logTopicId, req.payload)
    }

    private val appender = LogTopicAppenderImpl<TopicEntryPayload>(logBrokerRepository)
}