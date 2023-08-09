package io.hamal.backend.instance.web.topic

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTopicRoute(
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody createTopic: CreateTopicReq
    ): ResponseEntity<ApiSubmittedReqWithDomainId> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithDomainId(
                reqId = it.reqId,
                status = it.status,
                id = it.id.value
            )
        }, HttpStatus.ACCEPTED)
    }

}