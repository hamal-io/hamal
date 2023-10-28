package io.hamal.core.req.handler.topic

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.submitted_req.TopicAppendToSubmittedReq
import org.springframework.stereotype.Component

@Component
class AppendToTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<TopicAppendToSubmittedReq>(TopicAppendToSubmittedReq::class) {

    override fun invoke(req: TopicAppendToSubmittedReq) {
        val topic = eventBrokerRepository.getTopic(req.id)
        appender.append(req.cmdId(), topic, req.payload)
    }

    private val appender = ProtobufAppender(TopicEntryPayload::class, eventBrokerRepository)
}