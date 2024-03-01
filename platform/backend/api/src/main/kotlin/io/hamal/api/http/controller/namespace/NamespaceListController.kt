package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.namespace.NamespaceListPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeListPort
import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiNamespaceList
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceListController(
    private val namespaceTreeList: NamespaceTreeListPort,
    private val namespaceList: NamespaceListPort,
    private val namespaceSublist: NamespaceTreeGetSubTreePort
) {
    @GetMapping("/v1/workspaces/{workspaceId}/namespaces")
    fun list(
        @PathVariable("workspaceId") workspaceId: WorkspaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: NamespaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiNamespaceList> {
        return ResponseEntity.ok(namespaceTreeList(
            NamespaceTreeQuery(
                limit = Limit.one,
                workspaceIds = listOf(workspaceId)
            )
        ).let {
            it.map { tree ->
                namespaceList(
                    NamespaceQuery(
                        limit = Limit.all,
                        namespaceIds = tree.root.preorder()
                    )
                ).let { namespaces ->
                    if (namespaces.isEmpty()) {
                        ApiNamespaceList(
                            namespaces = listOf()
                        )
                    } else {
                        val namespacesById = namespaces.associateBy(Namespace::id)

                        val root = namespacesById[tree.root.value]!!.let { namespace ->
                            ApiNamespaceList.Namespace(
                                id = namespace.id,
                                parentId = namespace.id,
                                name = namespace.name,
                            )
                        }

                        ApiNamespaceList(
                            namespaces = (listOfNotNull(root).plus(assemble(root, tree.root, namespacesById)))
                                .filter { namespace -> namespace.id < afterId }
                                .reversed()
                                .take(limit.value)
                        )
                    }
                }
            }.firstOrNull() ?: throw NoSuchElementException("Workspace not found")
        })
    }

    @GetMapping("/v1/namespaces/{namespaceId}/sublist")
    fun sublist(
        @PathVariable("namespaceId") namespaceId: NamespaceId
    ): ResponseEntity<ApiNamespaceList> {
        return ResponseEntity.ok(
            ApiNamespaceList(
                namespaceList(
                    NamespaceQuery(
                        limit = Limit.all,
                        namespaceIds = namespaceSublist(namespaceId).values
                    )
                ).map { namespace ->
                    ApiNamespaceList.Namespace(
                        id = namespace.id,
                        parentId = namespace.id,
                        name = namespace.name,
                    )
                }.reversed()
            )
        )
    }
}

private fun assemble(
    parent: ApiNamespaceList.Namespace,
    tree: TreeNode<NamespaceId>,
    namespaces: Map<NamespaceId, Namespace>
): List<ApiNamespaceList.Namespace> {
    return tree.descendants.flatMap { child ->
        val namespace = namespaces[child.value]
        if (namespace == null) {
            listOf()
        } else {
            val newNamespace = ApiNamespaceList.Namespace(
                id = namespace.id,
                parentId = parent.id,
                name = NamespaceName(parent.name.value + "::" + namespace.name.value)
            )
            listOf(newNamespace).plus(assemble(newNamespace, child, namespaces))
        }
    }
}