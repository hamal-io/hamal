package io.hamal.repository.memory.record.namespace_tree

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeQueryRepository

internal object NamespaceTreeCurrentProjection {

    fun apply(tree: NamespaceTree) {
        projection[tree.id] = tree

        tree.root.preorder().forEach { namespaceId ->
            namespaceMapping[namespaceId] = tree.id
        }
    }

    fun find(namespaceId: NamespaceId): NamespaceTree? {
        val treeId = namespaceMapping[namespaceId] ?: return null
        return projection[treeId]
    }

    fun list(query: NamespaceTreeQueryRepository.NamespaceTreeQuery): List<NamespaceTree> {
        return projection.filter { query.treeIds.isEmpty() || it.key in query.treeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: NamespaceTreeQueryRepository.NamespaceTreeQuery): Count {
        return Count(
            projection.filter { query.treeIds.isEmpty() || it.key in query.treeIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter {
                    if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
                }.dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    private val projection = mutableMapOf<NamespaceTreeId, NamespaceTree>()
    private val namespaceMapping = mutableMapOf<NamespaceId, NamespaceTreeId>()

}