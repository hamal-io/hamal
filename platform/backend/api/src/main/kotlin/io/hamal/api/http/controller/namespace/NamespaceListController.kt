package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.adapter.NamespaceTreeListPort
import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiNamespaceList
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceListController(
    private val listNamespaceTrees: NamespaceTreeListPort,
    private val getNamespace: NamespaceGetPort
) {
    @GetMapping("/v1/workspaces/{workspaceId}/namespaces")
    fun listNamespace(
        @PathVariable("workspaceId") workspaceId: WorkspaceId
    ): ResponseEntity<ApiNamespaceList> {
        return ResponseEntity.ok(
            listNamespaceTrees(
                NamespaceTreeQuery(
                    limit = Limit.One,
                    workspaceIds = listOf(workspaceId)
                )
            ) { trees ->
                val tree = trees.first()
                // FIXME list namespaces with one query -- allows further filtering with query!
                val root = getNamespace(tree.root.value) {
                    ApiNamespaceList.Namespace(
                        id = it.id,
                        parentId = it.id,
                        name = it.name,
                    )
                }
                ApiNamespaceList(namespaces = listOf(root).plus(assemble(root, tree.root)))
            }
        )
    }

    private fun assemble(
        parent: ApiNamespaceList.Namespace,
        tree: TreeNode<NamespaceId>
    ): List<ApiNamespaceList.Namespace> {
        return tree.descendants.flatMap { child ->

            getNamespace(child.value) { ns ->
                val newNamespace = ApiNamespaceList.Namespace(
                    id = ns.id,
                    parentId = parent.parentId,
                    name = NamespaceName(parent.name.value + "::" + ns.name.value),
                )

                listOf(newNamespace).plus(assemble(newNamespace, child))
            }
        }
    }

    // FIXME fetch all namespaces at once and assemble together later
//    private fun assemble(node: TreeNode<NamespaceId>): ApiNamespaceList.Namespace {
//        return getNamespace(node.value) { namespace ->
//            ApiNamespaceList.Namespace(
//                id = namespace.id,
//                name = namespace.name,
//                children = node.descendants.map(::assemble)
//            )
//        }
//    }
}