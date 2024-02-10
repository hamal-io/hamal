package io.hamal.repository.sqlite.record.group

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository.CreateCmd
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.group.GroupCreatedRecord
import io.hamal.repository.record.group.GroupEntity
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateGroup : CreateDomainObject<GroupId, GroupRecord, Group> {
    override fun invoke(recs: List<GroupRecord>): Group {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is GroupCreatedRecord)

        var result = GroupEntity(
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

class GroupSqliteRepository(
    path: Path
) : RecordSqliteRepository<GroupId, GroupRecord, Group>(
    path = path,
    filename = "group.db",
    createDomainObject = CreateGroup,
    recordClass = GroupRecord::class,
    projections = listOf(
        ProjectionUniqueName,
        ProjectionCurrent
    )
), GroupRepository {

    override fun create(cmd: CreateCmd): Group {
        val groupId = cmd.groupId
        val cmdId = cmd.id

        return tx {
            if (commandAlreadyApplied(cmdId, groupId)) {
                versionOf(groupId, cmdId)
            } else {
                store(
                    GroupCreatedRecord(
                        entityId = groupId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )

                currentVersion(groupId)
                    .also { ProjectionUniqueName.upsert(this, it) }
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(groupId: GroupId): Group? {
        return ProjectionCurrent.find(connection, groupId)
    }

    override fun list(query: GroupQuery): List<Group> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: GroupQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}


