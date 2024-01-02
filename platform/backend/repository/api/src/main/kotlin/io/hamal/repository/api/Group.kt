package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName


data class Group(
    override val id: GroupId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val name: GroupName,
    val creatorId: AccountId
) : DomainObject<GroupId>


interface GroupRepository : GroupCmdRepository, GroupQueryRepository

interface GroupCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): Group

    data class CreateCmd(
        val id: CmdId,
        val groupId: GroupId,
        val name: GroupName,
        val creatorId: AccountId
    )
}

interface GroupQueryRepository {
    fun get(groupId: GroupId) = find(groupId) ?: throw NoSuchElementException("Group not found")
    fun find(groupId: GroupId): Group?
    fun list(groupIds: List<GroupId>): List<Group> = list(
        GroupQuery(
            limit = Limit.all,
            groupIds = groupIds,
        )
    )

    fun list(query: GroupQuery): List<Group>
    fun count(query: GroupQuery): ULong
    data class GroupQuery(
        var afterId: GroupId = GroupId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var groupIds: List<GroupId> = listOf()
    )
}


