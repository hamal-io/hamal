package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*

sealed class Topic : DomainObject<TopicId> {
    abstract val cmdId: CmdId
    abstract val name: TopicName
    abstract val logTopicId: LogTopicId
    abstract val groupId: GroupId

    abstract val type: TopicType

    /**
     * Topic only available for internal processing
     */
    data class Internal(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId
    ) : Topic() {
        override val type: TopicType = TopicType.Internal
    }

    /**
     * Topic only available within the same flow
     */
    data class Flow(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId,
        val flowId: FlowId,
    ) : Topic() {
        override val type: TopicType = TopicType.Flow
    }


    /**
     * Topic which is only available within the same group
     */
    data class Group(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId
    ) : Topic() {
        override val type: TopicType = TopicType.Group
    }

    /**
     * Topic which is publicly available to all users of hamal
     */
    data class Public(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId
    ) : Topic() {
        override val type: TopicType = TopicType.Public
    }

}

data class TopicEntry(
    val id: TopicEntryId, val payload: TopicEntryPayload
)

interface TopicCmdRepository : CmdRepository {

    fun create(cmd: TopicFlowCreateCmd): Topic.Flow

    fun create(cmd: TopicInternalCreateCmd): Topic.Internal

    data class TopicFlowCreateCmd(
        val id: CmdId,
        val topicId: TopicId,
        val name: TopicName,
        val flowId: FlowId,
        val groupId: GroupId,
        val logTopicId: LogTopicId
    )

    data class TopicInternalCreateCmd(
        val id: CmdId, val topicId: TopicId, val name: TopicName, val logTopicId: LogTopicId
    )
}


interface TopicQueryRepository {
    fun find(topicId: TopicId): Topic?
    fun get(topicId: TopicId) = find(topicId) ?: throw NoSuchElementException("Topic not found")

    fun getGroupTopic(groupId: GroupId, topicName: TopicName): Topic =
        findGroupTopic(groupId, topicName) ?: throw NoSuchElementException("Topic not found")

    fun findGroupTopic(groupId: GroupId, topicName: TopicName): Topic?

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
        var groupIds: List<GroupId> = listOf()
    )

    data class TopicEntryQuery(
        var afterId: TopicEntryId = TopicEntryId(0), var limit: Limit = Limit(1)
    )
}

interface TopicRepository : TopicCmdRepository, TopicQueryRepository