package io.hamal.repository.memory.record.extension

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<ExtensionId, Extension>() {

    override fun upsert(obj: Extension) {
        val currentExt = projection[obj.id]
        projection.remove(obj.id)

        val extInWorkspace = projection.values.filter { it.workspaceId == obj.workspaceId }
        if (extInWorkspace.any { it.name == obj.name }) {
            if (currentExt != null) {
                projection[currentExt.id] = currentExt
            }
            throw IllegalArgumentException("${obj.name} already exists in workspace ${obj.workspaceId}")
        }

        projection[obj.id] = obj
    }

    fun find(extensionId: ExtensionId): Extension? = projection[extensionId]

    fun list(query: ExtensionQuery): List<Extension> {
        return projection.filter { query.extensionIds.isEmpty() || it.key in query.extensionIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.intValue)
            .toList()
    }

    fun count(query: ExtensionQuery): Count {
        return Count(
            projection.filter { query.extensionIds.isEmpty() || it.key in query.extensionIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }
}