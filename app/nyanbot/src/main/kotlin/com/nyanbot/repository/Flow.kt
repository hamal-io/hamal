package com.nyanbot.repository

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId

class FlowId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class FlowName(override val value: String) : ValueObjectString()

class FlowTriggerId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class FlowTriggerType(override val value: String) : ValueObjectString()

data class FlowTrigger(
    override val id: FlowTriggerId,
    override val updatedAt: UpdatedAt,
    val type: FlowTriggerType
) : DomainObject<FlowTriggerId>

enum class FlowStatus {
    Active,
    Inactive
}

data class HamalMapping(
    val namespaceId: NamespaceId? = null,
    val funcId: FuncId? = null,
    val triggerId: TriggerId? = null
)


data class Flow(
    override val id: FlowId,
    override val updatedAt: UpdatedAt,
//    val accountId: AccountId,
//    val status: FlowStatus,
//    val name: FlowName,
//    val hamalMapping: HamalMapping,
//    val trigger: FlowTrigger
) : DomainObject<FlowId>


interface FlowRepository : FlowCmdRepository, FlowQueryRepository


interface FlowCmdRepository {

    fun create(cmd: CreateCmd): Flow

    data class CreateCmd(
        val flowId: FlowId,
        val accountId: AccountId,
        val trigger: FlowTrigger,
        val hamalMapping: HamalMapping
    )
}


interface FlowQueryRepository {
    fun get(flowId: FlowId) = find(flowId) ?: throw NoSuchElementException("Flow not found")
    fun find(flowId: FlowId): Flow?
    fun list(query: FlowQuery): List<Flow>
    fun count(query: FlowQuery): Count

    data class FlowQuery(
        var afterId: FlowId = FlowId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var flowIds: List<FlowId> = listOf()
    )
}
