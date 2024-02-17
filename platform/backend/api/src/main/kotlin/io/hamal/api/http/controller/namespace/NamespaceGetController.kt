package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespace
import io.hamal.repository.api.Namespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceGetController(
    private val retry: Retry,
    private val getNamespace: NamespaceGetPort
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(@PathVariable("namespaceId") namespaceId: NamespaceId): ResponseEntity<ApiNamespace> = retry {
        getNamespace(namespaceId) { namespace -> ResponseEntity.ok(assemble(namespace)) }
    }

    private fun assemble(namespace: Namespace): ApiNamespace {
        return ApiNamespace(
            id = namespace.id,
            name = namespace.name,
            children = namespace.children.map(::assemble)
        )
    }
}