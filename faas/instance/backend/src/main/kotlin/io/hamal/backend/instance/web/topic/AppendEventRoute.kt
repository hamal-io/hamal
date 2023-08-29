package io.hamal.backend.instance.web.topic

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.req.AppendEventReq
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AppendEventRoute(
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: LogBrokerRepository
) {
    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: EventPayload
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val result = submitRequest(
            AppendEventReq(
                topicId = topic.id,
                payload = topAppend
            )
        )
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}