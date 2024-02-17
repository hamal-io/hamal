package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.adapter.NamespaceTreeListPort
import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiNamespaceList
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceListController(
    private val listNamespaceTrees: NamespaceTreeListPort,
    private val getNamespace: NamespaceGetPort
) {
    @GetMapping("/v1/workspaces/{workspaceId}/namespaces")
    fun listNamespace(
        @PathVariable("workspaceId") workspaceId: WorkspaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiNamespaceList> {
        return ResponseEntity.ok(
            listNamespaceTrees(
                NamespaceTreeQuery(
                    limit = limit,
                    workspaceIds = listOf(workspaceId)
                )
            ) { trees ->
                ApiNamespaceList(namespaces = trees.map { tree -> assemble(tree.root) })
            }
        )
    }

    // FIXME fetch all namespaces at once and assemble together later
    private fun assemble(node: TreeNode<NamespaceId>): ApiNamespaceList.Namespace {
        return getNamespace(node.value) { namespace ->
            ApiNamespaceList.Namespace(
                id = namespace.id,
                name = namespace.name,
                children = node.descendants.map(::assemble)
            )
        }
    }
}