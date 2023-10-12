package io.hamal.repository.api

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val groupId: GroupId,
    val cmdId: CmdId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val codeVersion: CodeVersion
) : DomainObject<FuncId>

interface FuncRepository : FuncCmdRepository, FuncQueryRepository

interface FuncCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Func

    fun update(funcId: FuncId, cmd: UpdateCmd): Func

    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val groupId: GroupId,
        val namespaceId: NamespaceId,
        val name: FuncName,
        val inputs: FuncInputs,
        val codeId: CodeId,
        val codeVersion: CodeVersion
    )

    data class UpdateCmd(
        val id: CmdId,
        val namespaceId: NamespaceId? = null,
        val name: FuncName? = null,
        val inputs: FuncInputs? = null,
        val codeId: CodeId? = null,
        val codeVersion: CodeVersion? = null
    )
}

interface FuncQueryRepository {
    fun get(funcId: FuncId) = find(funcId) ?: throw NoSuchElementException("Func not found")
    fun find(funcId: FuncId): Func?
    fun list(query: FuncQuery): List<Func>
    fun list(funcIds: List<FuncId>): List<Func> = list(
        FuncQuery(
            limit = Limit.all,
            groupIds = listOf(),
            funcIds = funcIds,
        )
    )

    fun count(query: FuncQuery): ULong

    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var funcIds: List<FuncId> = listOf(),
        var groupIds: List<GroupId>
    )
}
