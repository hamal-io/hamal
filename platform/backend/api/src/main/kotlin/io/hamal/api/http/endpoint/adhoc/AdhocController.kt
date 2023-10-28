package io.hamal.api.http.endpoint.adhoc

import io.hamal.api.http.endpoint.req.Assembler.assemble
import io.hamal.core.adapter.AdhocInvokePort
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(private val invokeAdhoc: AdhocInvokePort) {
    @PostMapping("/v1/namespaces/{namespaceId}/adhoc")
    fun groupAdhoc(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiAdhocInvokeReq
    ): ResponseEntity<ApiSubmittedReq> =
        invokeAdhoc(namespaceId, req) {
            ResponseEntity(assemble(it), ACCEPTED)
        }
}
