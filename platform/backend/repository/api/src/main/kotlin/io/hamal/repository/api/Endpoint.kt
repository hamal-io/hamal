package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*

data class Endpoint(
    override val id: EndpointId,
    override val updatedAt: UpdatedAt,
    val groupId: GroupId,
    val cmdId: CmdId,
    val flowId: FlowId,
    val funcId: FuncId,
    val name: EndpointName
) : DomainObject<EndpointId>

interface EndpointRepository : EndpointCmdRepository, EndpointQueryRepository

interface EndpointCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Endpoint

    fun update(endpointId: EndpointId, cmd: UpdateCmd): Endpoint

    data class CreateCmd(
        val id: CmdId,
        val endpointId: EndpointId,
        val groupId: GroupId,
        val flowId: FlowId,
        val funcId: FuncId,
        val name: EndpointName
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: EndpointName? = null,
        val funcId: FuncId? = null
    )
}

interface EndpointQueryRepository {
    fun get(endpointId: EndpointId) = find(endpointId) ?: throw NoSuchElementException("Endpoint not found")
    fun find(endpointId: EndpointId): Endpoint?
    fun list(query: EndpointQuery): List<Endpoint>
    fun list(endpointIds: List<EndpointId>): List<Endpoint> = list(
        EndpointQuery(
            limit = Limit.all,
            groupIds = listOf(),
            endpointIds = endpointIds,
        )
    )

    fun count(query: EndpointQuery): ULong

    data class EndpointQuery(
        var afterId: EndpointId = EndpointId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var endpointIds: List<EndpointId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    )
}
