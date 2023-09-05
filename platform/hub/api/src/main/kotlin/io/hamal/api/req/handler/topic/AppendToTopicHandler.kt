package io.hamal.api.req.handler.topic

import io.hamal.api.req.ReqHandler
import io.hamal.api.req.handler.cmdId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.submitted_req.SubmittedAppendToTopicReq
import org.springframework.stereotype.Component

@Component
class AppendToTopicHandler(
    private val eventBrokerRepository: BrokerRepository
) : ReqHandler<SubmittedAppendToTopicReq>(SubmittedAppendToTopicReq::class) {

    override fun invoke(req: SubmittedAppendToTopicReq) {
        val topic = eventBrokerRepository.getTopic(req.id)
        appender.append(req.cmdId(), topic, req.payload)
    }

    private val appender = ProtobufAppender(TopicEntryPayload::class, eventBrokerRepository)
}