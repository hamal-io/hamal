package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespacePort
import io.hamal.core.adapter.NamespaceTreeGetSubTreePort
import io.hamal.core.component.Retry
import io.hamal.lib.common.TreeNode
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceGetController(
    private val retry: Retry,
    private val getNamespaceTree: NamespaceTreeGetSubTreePort,
    private val getNamespace: NamespacePort
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun get(@PathVariable("namespaceId") namespaceId: NamespaceId): ResponseEntity<ApiNamespace> = retry {
        getNamespaceTree(namespaceId) { namespace -> ResponseEntity.ok(assemble(namespace.root)) }
    }

    private fun assemble(node: TreeNode<NamespaceId>): ApiNamespace {
        return getNamespace(node.value) { namespace ->
            ApiNamespace(
                id = namespace.id,
                name = namespace.name
            )
        }
    }
}