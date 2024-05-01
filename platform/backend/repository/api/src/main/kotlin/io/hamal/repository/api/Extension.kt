package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExtensionId.Companion.ExtensionId

data class Extension(
    override val id: ExtensionId,
    override val updatedAt: UpdatedAt,
    override val workspaceId: WorkspaceId,
    val cmdId: CmdId,
    val name: ExtensionName,
    val code: ExtensionCode,
) : DomainObject<ExtensionId>, HasWorkspaceId

data class ExtensionCode(
    val id: CodeId,
    val version: CodeVersion
)

interface ExtensionRepository : ExtensionCmdRepository, ExtensionQueryRepository

interface ExtensionCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Extension
    fun update(extensionId: ExtensionId, cmd: UpdateCmd): Extension

    data class CreateCmd(
        val id: CmdId,
        val extensionId: ExtensionId,
        val workspaceId: WorkspaceId,
        val name: ExtensionName,
        val code: ExtensionCode
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: ExtensionName? = null,
        val code: ExtensionCode? = null
    )
}

interface ExtensionQueryRepository {
    fun get(extensionId: ExtensionId) = find(extensionId) ?: throw NoSuchElementException("Extension not found")
    fun find(extensionId: ExtensionId): Extension?
    fun list(query: ExtensionQuery): List<Extension>
    fun list(extensionIds: List<ExtensionId>): List<Extension> = list(
        ExtensionQuery(
            limit = Limit.all,
            workspaceIds = listOf(),
            extensionIds = extensionIds,
        )
    )

    fun count(query: ExtensionQuery): Count

    data class ExtensionQuery(
        var afterId: ExtensionId = ExtensionId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var extensionIds: List<ExtensionId> = listOf(),
        var workspaceIds: List<WorkspaceId>
    )
}