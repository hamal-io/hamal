package io.hamal.repository.sqlite.record.workspace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceCmdRepository.CreateCmd
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.api.WorkspaceRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.workspace.WorkspaceEntity
import io.hamal.repository.record.workspace.WorkspaceRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateWorkspace : CreateDomainObject<WorkspaceId, WorkspaceRecord, Workspace> {
    override fun invoke(recs: List<WorkspaceRecord>): Workspace {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is WorkspaceRecord.Created)

        var result = WorkspaceEntity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence(),
            name = firstRecord.name,
            creatorId = firstRecord.creatorId,
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class WorkspaceSqliteRepository(
    path: Path
) : RecordSqliteRepository<WorkspaceId, WorkspaceRecord, Workspace>(
    path = path,
    filename = "workspace.db",
    createDomainObject = CreateWorkspace,
    recordClass = WorkspaceRecord::class,
    projections = listOf(
        ProjectionUniqueName,
        ProjectionCurrent
    )
), WorkspaceRepository {

    override fun create(cmd: CreateCmd): Workspace {
        val workspaceId = cmd.workspaceId
        val cmdId = cmd.id

        return tx {
            if (commandAlreadyApplied(cmdId, workspaceId)) {
                versionOf(workspaceId, cmdId)
            } else {
                store(
                    WorkspaceRecord.Created(
                        entityId = workspaceId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )

                currentVersion(workspaceId)
                    .also { ProjectionUniqueName.upsert(this, it) }
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(workspaceId: WorkspaceId): Workspace? {
        return ProjectionCurrent.find(connection, workspaceId)
    }

    override fun list(query: WorkspaceQuery): List<Workspace> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: WorkspaceQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}


