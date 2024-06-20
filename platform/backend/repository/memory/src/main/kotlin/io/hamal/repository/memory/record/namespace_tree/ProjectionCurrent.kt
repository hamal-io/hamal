package io.hamal.repository.memory.record.namespace_tree

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeQueryRepository
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<NamespaceTreeId, NamespaceTree>() {

    override fun upsert(obj: NamespaceTree) {
        projection.values
            .filter { it.id != obj.id }
            .find { it.workspaceId == obj.workspaceId }
            ?.let { throw IllegalArgumentException("NamespaceTree already exists in workspace") }

        obj.root.preorder().forEach(namespaceMapping::remove)
        obj.root.preorder().forEach { namespaceId ->
            if (namespaceMapping[namespaceId] != null) {
                throw IllegalArgumentException("Namespace already exists in NamespaceTree")
            }
            namespaceMapping[namespaceId] = obj.id
        }

        projection[obj.id] = obj
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
            .take(query.limit.intValue)
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

    override fun clear() {
        projection.clear()
        namespaceMapping.clear()
    }

    private val namespaceMapping = mutableMapOf<NamespaceId, NamespaceTreeId>()
}