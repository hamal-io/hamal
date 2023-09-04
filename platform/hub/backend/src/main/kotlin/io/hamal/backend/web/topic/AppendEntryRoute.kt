package io.hamal.backend.web.topic

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.AppendEntryReq
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AppendEntryRoute(
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: BrokerRepository
) {
    @PostMapping("/v1/topics/{topicId}/entries")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEntryPayload
    ): ResponseEntity<HubSubmittedReqWithId> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val result = submitRequest(
            AppendEntryReq(
                topicId = topic.id,
                payload = topAppend
            )
        )
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}