package io.hamal.api.http.endpoint.namespace

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.NamespaceUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceUpdateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.NamespaceUpdateSubmitted
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
        @RequestBody req: ApiNamespaceUpdateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        updateNamespace(namespaceId, req, NamespaceUpdateSubmitted::accepted)
    }
}