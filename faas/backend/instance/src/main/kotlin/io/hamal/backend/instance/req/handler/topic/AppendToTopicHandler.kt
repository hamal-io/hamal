package io.hamal.backend.instance.req.handler.topic

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.req.SubmittedAppendToTopicReq
import org.springframework.stereotype.Component

@Component
class AppendToTopicHandler(
    private val eventBrokerRepository: LogBrokerRepository
) : ReqHandler<SubmittedAppendToTopicReq>(SubmittedAppendToTopicReq::class) {

    override fun invoke(req: SubmittedAppendToTopicReq) {
        val topic = eventBrokerRepository.getTopic(req.id)
        appender.append(req.cmdId(), topic, req.event)
    }

    private val appender = ProtobufAppender(Event::class, eventBrokerRepository)
}