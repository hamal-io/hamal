package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.GroupId
import kotlinx.serialization.Serializable

interface FlowRepository : FlowCmdRepository, FlowQueryRepository

interface FlowCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Flow

    fun update(flowId: FlowId, cmd: UpdateCmd): Flow

    data class CreateCmd(
        val id: CmdId,
        val flowId: FlowId,
        val groupId: GroupId,
        val name: FlowName,
        val inputs: FlowInputs
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: FlowName? = null,
        val inputs: FlowInputs? = null
    )
}

interface FlowQueryRepository {
    fun get(flowId: FlowId) = find(flowId) ?: throw NoSuchElementException("Flow not found")
    fun get(flowName: FlowName) = find(flowName) ?: throw NoSuchElementException("Flow not found")

    fun find(flowId: FlowId): Flow?
    fun find(flowName: FlowName): Flow?

    fun list(query: FlowQuery): List<Flow>
    fun list(flowIds: List<FlowId>) = list(
        FlowQuery(
            limit = Limit.all,
            groupIds = listOf(),
            flowIds = flowIds,
        )
    )

    fun count(query: FlowQuery): ULong

    data class FlowQuery(
        var afterId: FlowId = FlowId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    )
}

@Serializable
data class Flow(
    val cmdId: CmdId,
    override val id: FlowId,
    override val updatedAt: UpdatedAt,
    val groupId: GroupId,
    val name: FlowName,
    val inputs: FlowInputs
) : DomainObject<FlowId>

