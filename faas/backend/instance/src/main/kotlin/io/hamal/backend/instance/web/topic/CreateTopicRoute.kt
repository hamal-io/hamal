package io.hamal.backend.instance.web.topic

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CreateTopicRoute(
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody createTopic: CreateTopicReq
    ): ResponseEntity<SubmittedCreateTopicReq> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }

}