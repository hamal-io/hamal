package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.FlowId
import kotlinx.serialization.Serializable

@Serializable
data class Hook(
    override val id: HookId,
    override val updatedAt: UpdatedAt,
    val groupId: GroupId,
    val cmdId: CmdId,
    val flowId: FlowId,
    val name: HookName,
) : DomainObject<HookId>

interface HookRepository : HookCmdRepository, HookQueryRepository

interface HookCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Hook

    fun update(hookId: HookId, cmd: UpdateCmd): Hook

    data class CreateCmd(
        val id: CmdId,
        val hookId: HookId,
        val groupId: GroupId,
        val flowId: FlowId,
        val name: HookName
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: HookName? = null
    )
}

interface HookQueryRepository {
    fun get(hookId: HookId) = find(hookId) ?: throw NoSuchElementException("Hook not found")
    fun find(hookId: HookId): Hook?
    fun list(query: HookQuery): List<Hook>
    fun list(hookIds: List<HookId>): List<Hook> = list(
        HookQuery(
            limit = Limit.all,
            groupIds = listOf(),
            hookIds = hookIds,
        )
    )

    fun count(query: HookQuery): ULong

    data class HookQuery(
        var afterId: HookId = HookId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var hookIds: List<HookId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    )
}
