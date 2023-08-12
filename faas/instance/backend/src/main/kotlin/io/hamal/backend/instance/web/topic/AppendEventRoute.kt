package io.hamal.backend.instance.web.topic

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.req.AppendEventReq
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.type.TableType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.springframework.http.HttpStatus
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
        @RequestBody value: TableType
    ): ResponseEntity<ApiSubmittedReqWithDomainId> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val result = submitRequest(
            AppendEventReq(
                topicId = topic.id,
                event = Event(value)
            )
        )
        return ResponseEntity(result.let {
            ApiSubmittedReqWithDomainId(
                reqId = it.reqId,
                status = it.status,
                id = it.id.value
            )
        }, HttpStatus.ACCEPTED)
    }
}