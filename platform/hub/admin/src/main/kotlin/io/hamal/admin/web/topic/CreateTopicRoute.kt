package io.hamal.admin.web.topic

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateTopicReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateTopicRoute(
    private val submitRequest: SubmitAdminRequest
) {

    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody createTopic: AdminCreateTopicReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }


    @PostMapping("/v1/groups/{groupId}/topics")
    fun createGroupTopic(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createTopic: AdminCreateTopicReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }

}