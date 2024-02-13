package io.hamal.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*
import java.lang.reflect.Type

sealed class Topic : DomainObject<TopicId> {
    abstract val cmdId: CmdId
    abstract val name: TopicName
    abstract val logTopicId: LogTopicId
    abstract val groupId: GroupId
    abstract val namespaceId: NamespaceId

    abstract val type: TopicType

    object Adapter : JsonAdapter<Topic> {
        override fun serialize(
            src: Topic,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Topic {
            val type = json.asJsonObject.get("type").asString
            return context.deserialize(json, classMapping[type]!!.java)
        }

        private val classMapping = mapOf(
            TopicType.Internal.name to Internal::class,
            TopicType.Namespace.name to Namespace::class,
            TopicType.Group.name to Group::class,
            TopicType.Public.name to Public::class
        )
    }

    /**
     * Topic only available for internal processing
     */
    data class Internal(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId,
        override val namespaceId: NamespaceId
    ) : Topic() {
        override val type: TopicType = TopicType.Internal
    }

    /**
     * Topic which is only visible within the same namespace
     */
    data class Namespace(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId,
        override val namespaceId: NamespaceId
    ) : Topic() {
        override val type: TopicType = TopicType.Namespace
    }

    /**
     * Topic which is only visible within the same group
     */
    data class Group(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId,
        override val namespaceId: NamespaceId
    ) : Topic() {
        override val type: TopicType = TopicType.Group
    }

    /**
     * Topic which is publicly available to all users of hamal to consume,
     * only the group can write to
     */
    data class Public(
        override val cmdId: CmdId,
        override val id: TopicId,
        override val name: TopicName,
        override val logTopicId: LogTopicId,
        override val updatedAt: UpdatedAt,
        override val groupId: GroupId,
        override val namespaceId: NamespaceId
    ) : Topic() {
        override val type: TopicType = TopicType.Public
    }

}

data class TopicEvent(
    val id: TopicEventId,
    val payload: TopicEventPayload
)

interface TopicCmdRepository : CmdRepository {

    fun create(cmd: TopicCreateCmd): Topic

    data class TopicCreateCmd(
        val id: CmdId,
        val topicId: TopicId,
        val name: TopicName,
        val groupId: GroupId,
        val namespaceId: NamespaceId,
        val logTopicId: LogTopicId,
        val type: TopicType
    )
}


interface TopicQueryRepository {
    fun find(topicId: TopicId): Topic?
    fun get(topicId: TopicId) = find(topicId) ?: throw NoSuchElementException("Topic not found")

    fun getTopic(namespaceId: NamespaceId, topicName: TopicName): Topic =
        findTopic(namespaceId, topicName) ?: throw NoSuchElementException("Topic not found")

    fun findTopic(namespaceId: NamespaceId, topicName: TopicName): Topic?

    fun list(query: TopicQuery): List<Topic>
    fun count(query: TopicQuery): Count

    fun list(query: TopicEventQuery): List<TopicEvent>
    fun count(query: TopicEventQuery): Count

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var types: List<TopicType> = TopicType.values().toList(),
        var limit: Limit = Limit(1),
        var topicIds: List<TopicId> = listOf(),
        var names: List<TopicName> = listOf(),
        var groupIds: List<GroupId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf()
    )

    data class TopicEventQuery(
        var topicId: TopicId,
        var afterId: TopicEventId = TopicEventId(0),
        var limit: Limit = Limit(1)
    )
}

interface TopicRepository : TopicCmdRepository, TopicQueryRepository