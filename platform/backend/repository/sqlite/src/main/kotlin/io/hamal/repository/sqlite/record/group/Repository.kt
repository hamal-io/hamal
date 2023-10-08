package io.hamal.repository.sqlite.record.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository
import io.hamal.repository.api.GroupQueryRepository
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateGroup : CreateDomainObject<GroupId, GroupRecord, Group> {
    override fun invoke(recs: List<GroupRecord>): Group {
        TODO("Not yet implemented")
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

    override fun create(cmd: GroupCmdRepository.CreateCmd): Group {
        TODO("Not yet implemented")
    }

    override fun find(groupId: GroupId): Group? {
        TODO("Not yet implemented")
    }

    override fun list(query: GroupQueryRepository.GroupQuery): List<Group> {
        TODO("Not yet implemented")
    }

    override fun count(query: GroupQueryRepository.GroupQuery): ULong {
        TODO("Not yet implemented")
    }
}


