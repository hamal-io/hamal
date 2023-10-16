package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    override val id: SnippetId,
    val groupId: GroupId,
    val cmdId: CmdId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val codeValue: CodeValue,
    val accountId: AccountId
) : DomainObject<SnippetId>

interface SnippetRepository : SnippetCmdRepository, SnippetQueryRepository

interface SnippetCmdRepository : CmdRepository{
    fun create(cmd: CreateCmd): Snippet
    fun update(snippetId: SnippetId, cmd: UpdateCmd): Snippet

    data class CreateCmd(
        val id: CmdId,
        val snippetId: SnippetId,
        val groupId: GroupId,
        val inputs: SnippetInputs,
        val name: SnippetName,
        val codeValue: CodeValue,
        val accountId: AccountId // in RestController = 1
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: SnippetName? = null,
        val inputs: SnippetInputs? = null,
        val codeValue: CodeValue? = null
    )
}

interface SnippetQueryRepository {
    fun get(snippetId: SnippetId) = find(snippetId) ?: throw NoSuchElementException("Snippet not found")
    fun find(snippetId: SnippetId): Snippet?
    fun list(query: SnippetQuery): List<Snippet>
    fun count(query: SnippetQuery): ULong

    data class SnippetQuery(
        var afterId: SnippetId = SnippetId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var snippetIds: List<SnippetId> = listOf(),
        var groupIds: List<GroupId>
    )
}