package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
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
    private val namespaceTreeGetSub: NamespaceTreeGetSubTreePort,
    private val namespaceGet: NamespaceGetPort
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun get(@PathVariable("namespaceId") namespaceId: NamespaceId): ResponseEntity<ApiNamespace> = retry {
        namespaceTreeGetSub.invoke(namespaceId).let { tree ->
            ResponseEntity.ok(assemble(tree.root))
        }
    }

    private fun assemble(node: TreeNode<NamespaceId>): ApiNamespace {
        return namespaceGet(node.value).let { namespace ->
            ApiNamespace(
                id = namespace.id,
                name = namespace.name,
                features = namespace.features
            )
        }
    }
}