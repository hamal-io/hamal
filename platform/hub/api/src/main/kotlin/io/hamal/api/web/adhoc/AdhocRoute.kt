package io.hamal.api.web.adhoc

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdhocRoute(
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/groups/{groupId}/adhoc")
    fun adhoc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody adhocInvocation: InvokeAdhocReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        val result = submitRequest(groupId, adhocInvocation)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}
