package io.hamal.backend.web.adhoc

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class AdhocController(
    @Autowired private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody adhocInvocation: InvokeAdhocReq
    ): ResponseEntity<SubmittedInvokeAdhocReq> {
        val result = submitRequest(adhocInvocation)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}
