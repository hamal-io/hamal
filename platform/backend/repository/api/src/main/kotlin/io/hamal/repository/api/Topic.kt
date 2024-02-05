package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*

sealed class Topic : DomainObject<TopicId> {
    abstract val name: TopicName
    abstract val logTopicId: LogTopicId

    /**
     * Topic only available for internal processing
     */
    data class Internal(
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt
    ) : Topic()

    /**
     * Topic only available within the same flow
     */
    data class Flow(
        val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        val flowId: FlowId,
        val groupId: GroupId
    ) : Topic()

    /**
     * Topic which is only available within the same group
     */
    data class Group(
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        val groupId: GroupId
    ) : Topic()

    /**
     * Topic which is publicly available to all users of hamal
     */
    data class Public(
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        val groupId: GroupId
    ) : Topic()

}

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)

interface TopicCmdRepository : CmdRepository {

    fun create(cmd: CreateFlowTopicCmd): Topic.Flow

    data class CreateFlowTopicCmd(
        val id: CmdId,
        val topicId: TopicId,
        val name: TopicName,
        val flowId: FlowId,
        val groupId: GroupId,
        val logTopicId: LogTopicId
    )
}


interface TopicQueryRepository {
    fun get(topicId: TopicId) = find(topicId) ?: throw NoSuchElementException("Topic not found")
    fun find(topicId: TopicId): Topic?
    fun list(query: TopicQuery): List<Topic>
    fun count(query: TopicQuery): Count

    fun list(query: TopicEntryQuery): List<TopicEntry>
    fun count(query: TopicEntryQuery): Count

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var types: List<TopicType> = TopicType.values().toList(),
        var limit: Limit = Limit(1),
        var topicIds: List<TopicId> = listOf(),
        var names: List<TopicName> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf(),
    )

    data class TopicEntryQuery(
        var afterId: TopicEntryId = TopicEntryId(0),
        var limit: Limit = Limit(1)
    )
}

interface TopicRepository : TopicCmdRepository, TopicQueryRepository