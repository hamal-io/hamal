package io.hamal.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

interface FuncRepository : FuncCmdRepository, FuncQueryRepository

interface FuncCmdRepository {
    fun create(cmd: CreateCmd): Func

    fun update(funcId: FuncId, cmd: UpdateCmd): Func

    fun clear()

    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val groupId: GroupId,
        val namespaceId: NamespaceId,
        val name: FuncName,
        val inputs: FuncInputs,
        val code: CodeType,
    )

    data class UpdateCmd(
        val id: CmdId,
        val namespaceId: NamespaceId?,
        val name: FuncName?,
        val inputs: FuncInputs?,
        val code: CodeType?,
    )
}

interface FuncQueryRepository {
    fun get(funcId: FuncId) = find(funcId) ?: throw NoSuchElementException("Func not found")
    fun find(funcId: FuncId): Func?
    fun list(block: FuncQuery.() -> Unit): List<Func>
    fun list(funcIds: List<FuncId>): List<Func> = funcIds.map(::get)
    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}


@Serializable
data class Func(
    override val id: FuncId,
    val groupId: GroupId,
    val cmdId: CmdId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
) : DomainObject<FuncId>