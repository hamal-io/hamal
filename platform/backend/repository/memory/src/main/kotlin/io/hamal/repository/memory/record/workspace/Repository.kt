package io.hamal.repository.memory.record.workspace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceCmdRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.api.WorkspaceRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.workspace.CreateWorkspaceFromRecords
import io.hamal.repository.record.workspace.WorkspaceRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryWorkspaceRepository : RecordMemoryRepository<WorkspaceId, WorkspaceRecord, Workspace>(
    createDomainObject = CreateWorkspaceFromRecords,
    recordClass = WorkspaceRecord::class,
    projections = listOf(ProjectionCurrent())
), WorkspaceRepository {
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
                (currentVersion(workspaceId)).also(currentProjection::upsert)
            }
        }
    }

    override fun find(workspaceId: WorkspaceId): Workspace? = lock.withLock { currentProjection.find(workspaceId) }

    override fun list(query: WorkspaceQuery): List<Workspace> = lock.withLock { currentProjection.list(query) }

    override fun count(query: WorkspaceQuery): Count = lock.withLock { currentProjection.count(query) }

    override fun close() {
    }

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}
