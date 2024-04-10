package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.namespace_tree.NamespaceDeletePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceDeleteRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceDeleteController(
    private val retry: Retry,
    private val deleteNamespace: NamespaceDeletePort
) {
    @DeleteMapping("/v1/namespaces/{namespaceId}")
    fun delete(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiNamespaceDeleteRequest,
    ): ResponseEntity<ApiRequested> = retry {
        deleteNamespace(namespaceId, req).accepted()
    }
}