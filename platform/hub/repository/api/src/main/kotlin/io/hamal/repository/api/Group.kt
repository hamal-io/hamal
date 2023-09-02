package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import kotlinx.serialization.Serializable


@Serializable
data class Group(
    override val id: GroupId,
    val cmdId: CmdId,
    val name: GroupName,
    val creatorId: AccountId
) : DomainObject<GroupId>


interface GroupRepository : GroupCmdRepository, GroupQueryRepository

interface GroupCmdRepository {

    fun create(cmd: CreateCmd): Group

    fun clear()

    data class CreateCmd(
        val id: CmdId,
        val groupId: GroupId,
        val name: GroupName,
        val creatorId: AccountId
    )
}

interface GroupQueryRepository


