package io.hamal.repository.memory.record.namespace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<NamespaceId, Namespace>() {

    fun delete(obj: Namespace) {
        projection.remove(obj.id)
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
            .take(query.limit.intValue)
            .toList()
    }

    fun count(query: NamespaceQuery): Count {
        return Count.Count(
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
}

