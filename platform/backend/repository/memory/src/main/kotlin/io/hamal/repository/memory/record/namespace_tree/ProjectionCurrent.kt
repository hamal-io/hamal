package io.hamal.repository.memory.record.namespace_tree

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.repository.api.NamespaceTree

internal object NamespaceTreeCurrentProjection {

    fun apply(tree: NamespaceTree) {
        trees[tree.id] = tree

        //FIXME for all nodes - clean up
        namespaces[tree.root.value] = tree.id
    }

    fun find(namespaceId: NamespaceId): NamespaceTree? {
        val treeId = namespaces[namespaceId] ?: return null
        return trees[treeId]
    }

    private val trees = mutableMapOf<NamespaceTreeId, NamespaceTree>()
    private val namespaces = mutableMapOf<NamespaceId, NamespaceTreeId>()
}