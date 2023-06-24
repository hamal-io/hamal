package io.hamal.backend.web.event

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class CreateTopicController(
    @Autowired private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody createTopic: CreateTopicReq
    ): ResponseEntity<SubmittedCreateTopicReq> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }

}