package io.hamal.repository.memory.record.workspace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery

internal
object WorkspaceCurrentProjection {
    private val projection = mutableMapOf<WorkspaceId, Workspace>()

    fun apply(workspace: Workspace) {
        val currentWorkspace = projection[workspace.id]
        projection.remove(workspace.id)

        if (projection.values.any { it.name == workspace.name }) {
            if (currentWorkspace != null) {
                projection[currentWorkspace.id] = currentWorkspace
            }
            throw IllegalArgumentException("${workspace.name} already exists")
        }

        projection[workspace.id] = workspace
    }

    fun find(workspaceId: WorkspaceId): Workspace? = projection[workspaceId]

    fun list(query: WorkspaceQuery): List<Workspace> {
        return projection.filter { query.workspaceIds.isEmpty() || it.key in query.workspaceIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: WorkspaceQuery): Count {
        return Count(
            projection.filter { query.workspaceIds.isEmpty() || it.key in query.workspaceIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

