package io.hamal.core.adapter.topic

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface TopicEventAppendPort {
    operator fun invoke(req: TopicAppendEntryRequest): TopicAppendEventRequested
}

@Component
class TopicEventAppendAdapter(
    private val topicGet: TopicGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : TopicEventAppendPort {
    override fun invoke(req: TopicAppendEntryRequest): TopicAppendEventRequested {
        val topic = topicGet(req.topicId)
        return TopicAppendEventRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = req.topicId,
            payload = req.payload
        ).also(requestEnqueue::invoke)
    }

}