package io.hamal.core.component.topic

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.hub.HubAppendEntryReq
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.springframework.stereotype.Component

@Component
class AppendEntryToTopic(
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: BrokerRepository
) {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        topAppend: TopicEntryPayload,
        responseHandler: (SubmittedReq) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(topicId)
        return responseHandler(submitRequest(HubAppendEntryReq(topic.id, topAppend)))
    }
}