package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId

data class Func(
    override val id: FuncId,
    override val updatedAt: UpdatedAt,
    override val workspaceId: WorkspaceId,
    val cmdId: CmdId,
    override val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: FuncCode,
    val deployment: FuncDeployment
) : DomainObject<FuncId>, HasNamespaceId, HasWorkspaceId

data class FuncCode(
    val id: CodeId,
    val version: CodeVersion
)

data class FuncDeployment(
    val id: CodeId,
    val version: CodeVersion,
    val message: DeployMessage,
    val deployedAt: DeployedAt = DeployedAt.now()
) {
    fun toExecCode() = ExecCode(
        id = id,
        version = version,
        value = null
    )
}

interface FuncRepository : FuncCmdRepository, FuncQueryRepository

interface FuncCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Func

    fun update(funcId: FuncId, cmd: UpdateCmd): Func
    fun deploy(funcId: FuncId, cmd: DeployCmd): Func

    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val workspaceId: WorkspaceId,
        val namespaceId: NamespaceId,
        val name: FuncName,
        val inputs: FuncInputs,
        val codeId: CodeId,
        val codeVersion: CodeVersion
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: FuncName? = null,
        val inputs: FuncInputs? = null,
        val codeVersion: CodeVersion? = null
    )

    data class DeployCmd(
        val id: CmdId,
        val version: CodeVersion,
        val message: DeployMessage
    )
}

interface FuncQueryRepository {
    fun get(funcId: FuncId) = find(funcId) ?: throw NoSuchElementException("Func not found")
    fun find(funcId: FuncId): Func?
    fun list(query: FuncQuery): List<Func>
    fun list(funcIds: List<FuncId>): List<Func> = list(
        FuncQuery(
            limit = Limit.all,
            workspaceIds = listOf(),
            funcIds = funcIds,
        )
    )

    fun listDeployments(funcId: FuncId): List<FuncDeployment>

    fun count(query: FuncQuery): Count

    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var funcIds: List<FuncId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf()
    )


}
