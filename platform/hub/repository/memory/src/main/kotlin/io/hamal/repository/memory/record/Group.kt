package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.group.CreateGroupFromRecords
import io.hamal.repository.record.group.GroupCreationRecord
import io.hamal.repository.record.group.GroupRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentGroupProjection {
    private val projection = mutableMapOf<GroupId, Group>()

    fun apply(group: Group) {
        projection[group.id] = group
    }

    fun find(groupId: GroupId): Group? = projection[groupId]

    fun list(afterId: GroupId, limit: Limit): List<Group> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryGroupRepository : MemoryRecordRepository<GroupId, GroupRecord, Group>(
    createDomainObject = CreateGroupFromRecords,
    recordClass = GroupRecord::class
), GroupRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: GroupCmdRepository.CreateCmd): Group {
        return lock.withLock {
            val groupId = cmd.groupId
            if (commandAlreadyApplied(cmd.id, groupId)) {
                versionOf(groupId, cmd.id)
            } else {
                store(
                    GroupCreationRecord(
                        entityId = groupId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )
                (currentVersion(groupId)).also(CurrentGroupProjection::apply)
            }
        }
    }

    override fun find(groupId: GroupId): Group? = CurrentGroupProjection.find(groupId)

    override fun list(query: GroupQuery): List<Group> {
        return CurrentGroupProjection.list(query.afterId, query.limit)
    }

    override fun clear() {
        super.clear()
        CurrentGroupProjection.clear()
    }
}
