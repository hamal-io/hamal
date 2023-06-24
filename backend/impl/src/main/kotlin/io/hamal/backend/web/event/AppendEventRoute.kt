package io.hamal.backend.web.event

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.common.SnowflakeId
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
    @Autowired private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: String,
        @RequestHeader("Content-Type") contentType: String,
        @RequestBody body: ByteArray
    ): ResponseEntity<SubmittedAppendEventReq> {
        val result = submitRequest(
            AppendEventReq(
                topicId = TopicId(SnowflakeId(topicId.toLong())),
                contentType = ContentType(contentType),
                bytes = Content(body)
            )
        )
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}