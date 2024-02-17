package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.NamespaceTreeAppendPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceAppendController(
    private val retry: Retry,
    private val appendNamespace: NamespaceTreeAppendPort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/namespaces")
    fun createNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiNamespaceCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        appendNamespace(namespaceId, req, NamespaceAppendRequested::accepted)
    }
}