package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.group.CreateGroupFromRecords
import io.hamal.repository.record.group.GroupCreatedRecord
import io.hamal.repository.record.group.GroupRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object GroupCurrentProjection {
    private val projection = mutableMapOf<GroupId, Group>()

    fun apply(group: Group) {
        val currentGroup = projection[group.id]
        projection.remove(group.id)

        if (projection.values.any { it.name == group.name }) {
            if (currentGroup != null) {
                projection[currentGroup.id] = currentGroup
            }
            throw IllegalArgumentException("${group.name} already exists")
        }

        projection[group.id] = group
    }

    fun find(groupId: GroupId): Group? = projection[groupId]

    fun list(query: GroupQuery): List<Group> {
        return projection.filter { query.groupIds.isEmpty() || it.key in query.groupIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: GroupQuery): ULong {
        return projection.filter { query.groupIds.isEmpty() || it.key in query.groupIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryGroupRepository : RecordMemoryRepository<GroupId, GroupRecord, Group>(
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
                    GroupCreatedRecord(
                        entityId = groupId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        creatorId = cmd.creatorId
                    )
                )
                (currentVersion(groupId)).also(GroupCurrentProjection::apply)
            }
        }
    }

    override fun find(groupId: GroupId): Group? = lock.withLock { GroupCurrentProjection.find(groupId) }

    override fun list(query: GroupQuery): List<Group> = lock.withLock { return GroupCurrentProjection.list(query) }

    override fun count(query: GroupQuery): ULong = lock.withLock { GroupCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            GroupCurrentProjection.clear()
        }
    }

    override fun close() {
    }
}
