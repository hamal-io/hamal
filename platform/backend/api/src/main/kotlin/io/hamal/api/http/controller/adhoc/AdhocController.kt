package io.hamal.api.http.controller.adhoc

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.adhoc.AdhocInvokePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(
    private val invokeAdhoc: AdhocInvokePort,
    private val retry: Retry
) {
    @PostMapping("/v1/namespaces/{namespaceId}/adhoc")
    fun invoke(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiAdhocInvokeRequest
    ): ResponseEntity<ApiRequested> = retry { invokeAdhoc(namespaceId, req).accepted() }
}