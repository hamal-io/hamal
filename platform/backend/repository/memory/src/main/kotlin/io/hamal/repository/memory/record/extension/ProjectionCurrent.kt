package io.hamal.repository.memory.record.extension

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery

internal object ExtensionCurrentProjection {
    private val projection = mutableMapOf<ExtensionId, Extension>()
    fun apply(ext: Extension) {
        val currentExt = projection[ext.id]
        projection.remove(ext.id)

        val extInWorkspace = projection.values.filter { it.workspaceId == ext.workspaceId }
        if (extInWorkspace.any { it.name == ext.name }) {
            if (currentExt != null) {
                projection[currentExt.id] = currentExt
            }
            throw IllegalArgumentException("${ext.name} already exists in workspace ${ext.workspaceId}")
        }

        projection[ext.id] = ext
    }

    fun find(extensionId: ExtensionId): Extension? = projection[extensionId]

    fun list(query: ExtensionQuery): List<Extension> {
        return projection.filter { query.extensionIds.isEmpty() || it.key in query.extensionIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
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

    fun clear() {
        projection.clear()
    }
}