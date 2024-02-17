package io.hamal.api.http.controller.namespace

import io.hamal.core.component.Retry
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceCreateController(
    private val retry: Retry,
//    private val createNamespace: NamespaceAppendPort
) {
//    @PostMapping("/v1/namespaces/{namespaceId}/namespaces")
//    fun createNamespace(
//        @PathVariable("namespaceId") namespaceId: NamespaceId,
//        @RequestBody req: ApiNamespaceCreateRequest
//    ): ResponseEntity<ApiRequested> = retry {
//        createNamespace(namespaceId, req, NamespaceCreateRequested::accepted)
//    }
}