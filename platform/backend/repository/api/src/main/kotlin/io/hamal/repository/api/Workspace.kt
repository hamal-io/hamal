package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName


data class Workspace(
    override val id: WorkspaceId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val name: WorkspaceName,
    val creatorId: AccountId
) : DomainObject<WorkspaceId>, HasAccountId, HasWorkspaceId {
    override val accountId: AccountId get() = creatorId
    override val workspaceId: WorkspaceId get() = id
}


interface WorkspaceRepository : WorkspaceCmdRepository, WorkspaceQueryRepository

interface WorkspaceCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): Workspace

    data class CreateCmd(
        val id: CmdId,
        val workspaceId: WorkspaceId,
        val name: WorkspaceName,
        val creatorId: AccountId
    )
}

interface WorkspaceQueryRepository {
    fun get(workspaceId: WorkspaceId) = find(workspaceId) ?: throw NoSuchElementException("Workspace not found")
    fun find(workspaceId: WorkspaceId): Workspace?
    fun list(workspaceIds: List<WorkspaceId>): List<Workspace> = list(
        WorkspaceQuery(
            limit = Limit.all,
            workspaceIds = workspaceIds,
        )
    )

    fun list(query: WorkspaceQuery): List<Workspace>
    fun count(query: WorkspaceQuery): Count
    data class WorkspaceQuery(
        var afterId: WorkspaceId = WorkspaceId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var workspaceIds: List<WorkspaceId> = listOf(),
        var accountIds: List<AccountId> = listOf()
    )
}


