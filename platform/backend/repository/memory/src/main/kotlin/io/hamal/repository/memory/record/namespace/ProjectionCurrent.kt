package io.hamal.repository.memory.record.namespace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery

internal object NamespaceCurrentProjection {
    private val projection = mutableMapOf<NamespaceId, Namespace>()

    fun apply(namespace: Namespace) {
        val currentNamespace = projection[namespace.id]
        projection.remove(namespace.id)

        val namespacesInWorkspace = projection.values.filter { it.workspaceId == namespace.workspaceId }
        if (namespacesInWorkspace.any { it.name == namespace.name }) {
            if (currentNamespace != null) {
                projection[currentNamespace.id] = currentNamespace
            }
            throw IllegalArgumentException("${namespace.name} already exists")
        }

        projection[namespace.id] = namespace
    }

    fun find(namespaceId: NamespaceId): Namespace? = projection[namespaceId]
    fun find(namespaceName: NamespaceName): Namespace? = projection.values.find { it.name == namespaceName }

    fun list(query: NamespaceQuery): List<Namespace> {
        return projection.filter { query.namespaceIds.isEmpty() || it.key in query.namespaceIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: NamespaceQuery): Count {
        return Count(
            projection.filter { query.namespaceIds.isEmpty() || it.key in query.namespaceIds }
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

    fun clear() {
        projection.clear()
    }
}

