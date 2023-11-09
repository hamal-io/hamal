package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Code(
    override val id: CodeId,
    override val updatedAt: DomainUpdatedAt,
    val groupId: GroupId,
    val cmdId: CmdId,
    val version: CodeVersion,
    val value: CodeValue,
    var type: CodeType,
) : DomainObject<CodeId>, DomainObjectWithUpdate


interface CodeRepository : CodeCmdRepository, CodeQueryRepository

interface CodeCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Code
    fun update(codeId: CodeId, cmd: UpdateCmd): Code

    data class CreateCmd(
        val id: CmdId,
        val codeId: CodeId,
        val groupId: GroupId,
        val value: CodeValue,
        val type: CodeType = CodeType.Lua54
    )

    data class UpdateCmd(
        val id: CmdId,
        val value: CodeValue? = null,
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
            groupIds = listOf(),
            codeIds = codeIds,
        )
    )

    fun count(query: CodeQuery): ULong

    data class CodeQuery(
        var afterId: CodeId = CodeId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var codeIds: List<CodeId> = listOf(),
        var groupIds: List<GroupId>
    )

}


