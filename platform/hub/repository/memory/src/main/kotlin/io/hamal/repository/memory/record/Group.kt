package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.record.group.GroupCreationRecord
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.record.group.createEntity
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

object MemoryGroupRepository : BaseRecordRepository<GroupId, GroupRecord>(), GroupRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: GroupCmdRepository.CreateCmd): Group {
        return lock.withLock {
            val groupId = cmd.groupId
            if (contains(groupId)) {
                versionOf(groupId, cmd.id)
            } else {
                addRecord(
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

    override fun list(block: GroupQuery.() -> Unit): List<Group> {
        val query = GroupQuery().also(block)
        return CurrentGroupProjection.list(query.afterId, query.limit)
    }

    override fun clear() {
        super.clear()
        CurrentGroupProjection.clear()
    }
}

private fun MemoryGroupRepository.currentVersion(id: GroupId): Group {
    return listRecords(id)
        .createEntity()
        .toDomainObject()
}

private fun MemoryGroupRepository.commandAlreadyApplied(id: GroupId, cmdId: CmdId) =
    listRecords(id).any { it.cmdId == cmdId }

private fun MemoryGroupRepository.versionOf(id: GroupId, cmdId: CmdId): Group {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}