package io.hamal.core.request.handler.topic

import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicAppendToRequested
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.repository.api.log.AppenderImpl
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.stereotype.Component

@Component
class AppendToHandler(
    private val eventBrokerRepository: BrokerRepository
) : io.hamal.core.request.RequestHandler<TopicAppendToRequested>(TopicAppendToRequested::class) {

    override fun invoke(req: TopicAppendToRequested) {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        appender.append(req.cmdId(), topic, req.payload)
    }

    private val appender = AppenderImpl<TopicEntryPayload>(eventBrokerRepository)
}