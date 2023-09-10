package io.hamal.admin.web.adhoc

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocRoute(
    private val submitRequest: SubmitAdminRequest
) {
    @PostMapping("/v1/groups/{groupId}/adhoc")
    fun adhoc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody adhocInvocation: AdminInvokeAdhocReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val result = submitRequest(groupId, adhocInvocation)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}
