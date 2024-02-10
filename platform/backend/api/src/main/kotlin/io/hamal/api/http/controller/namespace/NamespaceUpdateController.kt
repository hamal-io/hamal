package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.NamespaceUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.NamespaceUpdateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceUpdateController(
    private val retry: Retry,
    private val updateNamespace: NamespaceUpdatePort
) {
    @PatchMapping("/v1/namespaces/{namespaceId}")
    fun updateNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiNamespaceUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        updateNamespace(namespaceId, req, NamespaceUpdateRequested::accepted)
    }
}