package io.hamal.backend.web.namespace

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateNamespaceRoute(
    private val request: SubmitRequest,
) {
    @PostMapping("/v1/namespaces")
    fun createNamespace(
        @RequestBody createNamespace: CreateNamespaceReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val result = request(createNamespace)
        return ResponseEntity(
            result.let {
                ApiSubmittedReqWithId(
                    reqId = it.reqId,
                    status = it.status,
                    id = it.id
                )
            }, ACCEPTED
        )
    }
}