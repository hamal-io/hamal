package io.hamal.api.http.controller.adhoc

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AdhocInvokePort
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(private val invokeAdhoc: AdhocInvokePort) {
    @PostMapping("/v1/namespaces/{namespaceId}/adhoc")
    fun invokeAdhoc(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiAdhocInvokeReq
    ): ResponseEntity<ApiSubmitted> = invokeAdhoc(namespaceId, req, ExecInvokeSubmitted::accepted)
}