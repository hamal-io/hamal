package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.req.AppendEventReq
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AppendEventRoute(
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: LogBrokerRepository<*>
) {
    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody value: TableValue
    ): ResponseEntity<SubmittedAppendEventReq> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val result = submitRequest(
            AppendEventReq(
                topicId = topic.id,
                event = Event(value)
            )
        )
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}