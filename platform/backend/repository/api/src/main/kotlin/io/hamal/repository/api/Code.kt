package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeId.Companion.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.WorkspaceId

data class Code(
    override val id: CodeId,
    override val updatedAt: UpdatedAt,
    override val workspaceId: WorkspaceId,
    val cmdId: CmdId,
    val version: CodeVersion,
    val value: ValueCode,
    var type: CodeType,
) : DomainObject<CodeId>, HasWorkspaceId

interface CodeRepository : CodeCmdRepository, CodeQueryRepository

interface CodeCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Code
    fun update(codeId: CodeId, cmd: UpdateCmd): Code

    data class CreateCmd(
        val id: CmdId,
        val codeId: CodeId,
        val workspaceId: WorkspaceId,
        val value: ValueCode,
        val type: CodeType = CodeType.Lua54
    )

    data class UpdateCmd(
        val id: CmdId,
        val value: ValueCode? = null,
    )
}

interface CodeQueryRepository {
    fun get(codeId: CodeId) = find(codeId)
        ?: throw NoSuchElementException("Code not found")

    fun get(codeId: CodeId, codeVersion: CodeVersion) = find(codeId, codeVersion)
        ?: throw NoSuchElementException("Code not found")

    fun find(codeId: CodeId): Code?
    fun find(codeId: CodeId, codeVersion: CodeVersion): Code?
    fun list(query: CodeQuery): List<Code>
    fun list(codeIds: List<CodeId>): List<Code> = list(
        CodeQuery(
            limit = Limit.all,
            workspaceIds = listOf(),
            codeIds = codeIds,
        )
    )

    fun count(query: CodeQuery): Count

    data class CodeQuery(
        var afterId: CodeId = CodeId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var codeIds: List<CodeId> = listOf(),
        var workspaceIds: List<WorkspaceId>
    )

}


