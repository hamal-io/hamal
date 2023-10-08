package io.hamal.repository.sqlite.record.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository.CreateCmd
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.group.GroupCreationRecord
import io.hamal.repository.record.group.GroupEntity
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateGroup : CreateDomainObject<GroupId, GroupRecord, Group> {
    override fun invoke(recs: List<GroupRecord>): Group {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is GroupCreationRecord)

        var result = GroupEntity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence(),
            name = firstRecord.name,
            creatorId = firstRecord.creatorId
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteGroupRepository(
    config: Config
) : SqliteRecordRepository<GroupId, GroupRecord, Group>(
    config = config,
    createDomainObject = CreateGroup,
    recordClass = GroupRecord::class,
    projections = listOf(
        ProjectionCurrent
    )
), GroupRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "group.db"
    }

    override fun create(cmd: CreateCmd): Group {
        val groupId = cmd.groupId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, groupId)) {
                versionOf(groupId, cmdId)
            } else {
                store(
                    GroupCreationRecord(
                        entityId = groupId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )

                currentVersion(groupId)
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

    override fun count(query: GroupQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}


