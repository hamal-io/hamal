package io.hamal.backend.instance.req.handler.event

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import org.springframework.stereotype.Component

@Component
class AppendEventHandler<TOPIC : LogTopic>(
    private val eventBrokerRepository: LogBrokerRepository<TOPIC>
) : ReqHandler<SubmittedAppendEventReq>(SubmittedAppendEventReq::class) {
    override fun invoke(req: SubmittedAppendEventReq) {
        val topic = eventBrokerRepository.get(req.topicId)
        appender.append(req.cmdId(), topic, req.event)
    }

    private val appender = ProtobufAppender(Event::class, eventBrokerRepository)
}