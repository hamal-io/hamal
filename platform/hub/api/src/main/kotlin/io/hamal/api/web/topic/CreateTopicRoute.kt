package io.hamal.api.web.topic

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubCreateTopicReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTopicRoute(
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/groups/{groupId}/topics")
    fun createTopic(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createTopic: HubCreateTopicReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }

}