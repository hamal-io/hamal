package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.CmdRepository

@Deprecated("")
interface BrokerTopicsRepository : CmdRepository {
    fun create(cmdId: CmdId, toCreate: TopicToCreate): DepTopic
    fun find(flowId: FlowId, name: TopicName): DepTopic?
    fun find(id: TopicId): DepTopic?
    fun list(query: TopicQuery): List<DepTopic>
    fun count(query: TopicQuery): ULong

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName,
        val flowId: FlowId,
        val groupId: GroupId
    )

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var names: List<TopicName> = listOf(),
        var limit: Limit = Limit(1),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    )
}
