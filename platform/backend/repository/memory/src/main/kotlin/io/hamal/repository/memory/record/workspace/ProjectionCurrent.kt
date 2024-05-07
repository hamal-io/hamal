package io.hamal.repository.memory.record.workspace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory<WorkspaceId, Workspace> {

    override fun upsert(obj: Workspace) {
        val currentWorkspace = projection[obj.id]
        projection.remove(obj.id)

        if (projection.values.any { it.name == obj.name }) {
            if (currentWorkspace != null) {
                projection[currentWorkspace.id] = currentWorkspace
            }
            throw IllegalArgumentException("${obj.name} already exists")
        }

        projection[obj.id] = obj
    }

    fun find(workspaceId: WorkspaceId): Workspace? = projection[workspaceId]

    fun list(query: WorkspaceQuery): List<Workspace> {
        return projection.filter { query.workspaceIds.isEmpty() || it.key in query.workspaceIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.accountIds.isEmpty()) true else query.accountIds.contains(it.creatorId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.intValue)
            .toList()
    }

    fun count(query: WorkspaceQuery): Count {
        return Count(
            projection.filter { query.workspaceIds.isEmpty() || it.key in query.workspaceIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.accountIds.isEmpty()) true else query.accountIds.contains(it.creatorId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    override fun clear() {
        projection.clear()
    }

    private val projection = mutableMapOf<WorkspaceId, Workspace>()
}

