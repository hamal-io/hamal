package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Extension(
    override val id: ExtensionId,
    override val updatedAt: UpdatedAt,
    val groupId: GroupId,
    val cmdId: CmdId,
    val name: ExtensionName,
    val code: ExtensionCode,
) : DomainObject<ExtensionId>

@Serializable
data class ExtensionCode(
    val id: CodeId,
    val version: CodeVersion
)

interface ExtensionRepository : ExtensionCmdRepository, ExtensionQueryRepository

interface ExtensionCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Extension
    fun update(extId: ExtensionId, cmd: UpdateCmd): Extension

    data class CreateCmd(
        val id: CmdId,
        val extId: ExtensionId,
        val groupId: GroupId,
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
    fun get(extId: ExtensionId) = find(extId) ?: throw NoSuchElementException("Extension not found")
    fun find(extId: ExtensionId): Extension?
    fun list(query: ExtensionQuery): List<Extension>
    fun list(extIds: List<ExtensionId>): List<Extension> = list(
        ExtensionQuery(
            limit = Limit.all,
            groupIds = listOf(),
            extIds = extIds,
        )
    )

    fun count(query: ExtensionQuery): ULong

    data class ExtensionQuery(
        var afterId: ExtensionId = ExtensionId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var extIds: List<ExtensionId> = listOf(),
        var groupIds: List<GroupId>
    )
}