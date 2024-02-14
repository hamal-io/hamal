package io.hamal.repository.record.workspace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.repository.api.Workspace
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class WorkspaceEntity(
    override val id: WorkspaceId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var name: WorkspaceName?,
    var creatorId: AccountId?

) : RecordEntity<WorkspaceId, WorkspaceRecord, Workspace> {

    override fun apply(rec: WorkspaceRecord): WorkspaceEntity {
        return when (rec) {
            is WorkspaceRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                creatorId = rec.creatorId,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Workspace {
        return Workspace(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            name = name!!,
            creatorId = creatorId!!
        )
    }
}

fun List<WorkspaceRecord>.createEntity(): WorkspaceEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is WorkspaceRecord.Created)

    var result = WorkspaceEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        name = firstRecord.name,
        creatorId = firstRecord.creatorId,
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateWorkspaceFromRecords : CreateDomainObject<WorkspaceId, WorkspaceRecord, Workspace> {
    override fun invoke(recs: List<WorkspaceRecord>): Workspace {
        return recs.createEntity().toDomainObject()
    }
}