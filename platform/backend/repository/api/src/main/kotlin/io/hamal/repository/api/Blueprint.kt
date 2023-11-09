package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    override val id: BlueprintId,
    val cmdId: CmdId,
    val groupId: GroupId,
    val creatorId: AccountId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
) : DomainObject<BlueprintId>

interface BlueprintRepository : BlueprintCmdRepository, BlueprintQueryRepository

interface BlueprintCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Blueprint
    fun update(blueprintId: BlueprintId, cmd: UpdateCmd): Blueprint

    data class CreateCmd(
        val id: CmdId,
        val blueprintId: BlueprintId,
        val groupId: GroupId,
        val creatorId: AccountId,
        val inputs: BlueprintInputs,
        val name: BlueprintName,
        val value: CodeValue
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: BlueprintName? = null,
        val inputs: BlueprintInputs? = null,
        val value: CodeValue? = null
    )
}

interface BlueprintQueryRepository {
    fun get(blueprintId: BlueprintId) = find(blueprintId) ?: throw NoSuchElementException("Blueprint not found")
    fun find(blueprintId: BlueprintId): Blueprint?
    fun list(query: BlueprintQuery): List<Blueprint>
    fun list(blueprintIds: List<BlueprintId>): List<Blueprint> = list(
        BlueprintQuery(
            limit = Limit.all,
            groupIds = listOf(),
            blueprintIds = blueprintIds
        )
    )

    fun count(query: BlueprintQuery): ULong

    data class BlueprintQuery(
        var afterId: BlueprintId = BlueprintId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var blueprintIds: List<BlueprintId> = listOf(),
        var groupIds: List<GroupId>
    )
}