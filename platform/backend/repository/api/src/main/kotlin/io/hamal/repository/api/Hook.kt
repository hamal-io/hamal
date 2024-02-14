package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName

data class Hook(
    override val id: HookId,
    override val updatedAt: UpdatedAt,
    val workspaceId: WorkspaceId,
    val cmdId: CmdId,
    val namespaceId: NamespaceId,
    val name: HookName,
) : DomainObject<HookId>

interface HookRepository : HookCmdRepository, HookQueryRepository

interface HookCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Hook

    fun update(hookId: HookId, cmd: UpdateCmd): Hook

    data class CreateCmd(
        val id: CmdId,
        val hookId: HookId,
        val workspaceId: WorkspaceId,
        val namespaceId: NamespaceId,
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
            workspaceIds = listOf(),
            hookIds = hookIds,
        )
    )

    fun count(query: HookQuery): Count

    data class HookQuery(
        var afterId: HookId = HookId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var hookIds: List<HookId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf()
    )
}
