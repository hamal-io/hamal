package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceCmdRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.api.WorkspaceRepository
import io.hamal.repository.record.workspace.CreateWorkspaceFromRecords
import io.hamal.repository.record.workspace.WorkspaceRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object WorkspaceCurrentProjection {
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

class MemoryWorkspaceRepository : RecordMemoryRepository<WorkspaceId, WorkspaceRecord, Workspace>(
    createDomainObject = CreateWorkspaceFromRecords,
    recordClass = WorkspaceRecord::class
), WorkspaceRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: WorkspaceCmdRepository.CreateCmd): Workspace {
        return lock.withLock {
            val workspaceId = cmd.workspaceId
            if (commandAlreadyApplied(cmd.id, workspaceId)) {
                versionOf(workspaceId, cmd.id)
            } else {
                store(
                    WorkspaceRecord.Created(
                        entityId = workspaceId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )
                (currentVersion(workspaceId)).also(WorkspaceCurrentProjection::apply)
            }
        }
    }

    override fun find(workspaceId: WorkspaceId): Workspace? = lock.withLock { WorkspaceCurrentProjection.find(workspaceId) }

    override fun list(query: WorkspaceQuery): List<Workspace> = lock.withLock { return WorkspaceCurrentProjection.list(query) }

    override fun count(query: WorkspaceQuery): Count = lock.withLock { WorkspaceCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            WorkspaceCurrentProjection.clear()
        }
    }

    override fun close() {
    }
}
