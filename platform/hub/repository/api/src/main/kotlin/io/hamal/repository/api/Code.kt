package io.hamal.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

@Serializable
class CodeValue {
    val code: String = ""
}

@Serializable(with = CodeId.Serializer::class)
class CodeId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<CodeId>(::CodeId)
}

@Serializable
data class Code(
    override val id: CodeId,
    val groupId: GroupId,
    val cmdId: CmdId,
    val code: CodeValue
) : DomainObject<CodeId>


interface CodeRepository : CodeCmdRepository, CodeQueryRepository

interface CodeCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Code
    fun update(): Code

    data class CreateCmd(
        val id: CmdId,
        val codeId: CodeId,
        val groupId: GroupId,
        val code: CodeValue,
    )

    data class UpdateCmd(
        val id: CmdId,
        val code: CodeValue? = null,
    )
}

interface CodeQueryRepository {
    fun get(codeId: CodeId) = find(codeId) ?: throw NoSuchElementException("Code not found")
    fun find(codeId: CodeId): Code?
    fun list(): List<Code>

    /*fun list(codeIds: List<CodeId>): List<Code> = list(
        CodeQuery(
            limit = Limit.all,
            groupIds = listOf(),
            codeIds = codeIds,
        )
    )
    */
    fun count(query: CodeQuery): ULong

    data class CodeQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var funcIds: List<CodeId> = listOf(),
        var groupIds: List<GroupId>
    )

}


