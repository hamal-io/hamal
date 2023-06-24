package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.lib.domain.req.AppendEventReq
import io.hamal.lib.domain.req.SubmittedAppendEventReq
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class AppendEventRoute(
    @Autowired private val submitRequest: SubmitRequest,
    @Autowired private val eventQueryService: EventQueryService<*>
) {
    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestHeader("Content-Type") contentType: ContentType,
        @RequestBody body: ByteArray
    ): ResponseEntity<SubmittedAppendEventReq> {
        val topic = eventQueryService.getTopic(topicId)
        val result = submitRequest(
            AppendEventReq(
                topicId = topic.id,
                contentType = contentType,
                bytes = Content(body)
            )
        )
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}