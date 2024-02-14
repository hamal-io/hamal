package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*

data class Blueprint(
    override val id: BlueprintId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val creatorId: AccountId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue,
    val description: BlueprintDescription
) : DomainObject<BlueprintId>

interface BlueprintRepository : BlueprintCmdRepository, BlueprintQueryRepository

interface BlueprintCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Blueprint
    fun update(blueprintId: BlueprintId, cmd: UpdateCmd): Blueprint

    data class CreateCmd(
        val id: CmdId,
        val blueprintId: BlueprintId,
        val creatorId: AccountId,
        val inputs: BlueprintInputs,
        val name: BlueprintName,
        val value: CodeValue,
        val description: BlueprintDescription
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: BlueprintName? = null,
        val inputs: BlueprintInputs? = null,
        val value: CodeValue? = null,
        val description: BlueprintDescription
    )
}

interface BlueprintQueryRepository {
    fun get(blueprintId: BlueprintId) = find(blueprintId) ?: throw NoSuchElementException("Blueprint not found")
    fun find(blueprintId: BlueprintId): Blueprint?
    fun list(query: BlueprintQuery): List<Blueprint>
    fun list(blueprintIds: List<BlueprintId>): List<Blueprint> = list(
        BlueprintQuery(
            limit = Limit.all,
            blueprintIds = blueprintIds
        )
    )

    fun count(query: BlueprintQuery): Count

    data class BlueprintQuery(
        var afterId: BlueprintId = BlueprintId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var blueprintIds: List<BlueprintId> = listOf(),
    )
}