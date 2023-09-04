package io.hamal.backend.web.adhoc

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdhocRoute(
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody adhocInvocation: InvokeAdhocReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        val result = submitRequest(adhocInvocation)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}
