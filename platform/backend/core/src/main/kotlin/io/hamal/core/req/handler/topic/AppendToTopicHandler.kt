package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.submitted_req.TopicAppendToSubmitted
import org.springframework.stereotype.Component

@Component
class AppendToTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicAppendToSubmitted>(TopicAppendToSubmitted::class) {

    override fun invoke(req: TopicAppendToSubmitted) {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        appender.append(req.cmdId(), topic, req.payload)
    }

    private val appender = ProtobufAppender(TopicEntryPayload::class, eventBrokerRepository)
}