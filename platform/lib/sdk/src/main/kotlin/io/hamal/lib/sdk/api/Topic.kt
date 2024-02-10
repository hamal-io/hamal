package io.hamal.lib.sdk.api

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTopicService.TopicQuery
import io.hamal.lib.sdk.fold

data class ApiTopicCreateRequest(
    override val name: TopicName
) : TopicCreateRequest

data class ApiTopicGroupCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val topicId: TopicId,
    val groupId: GroupId
) : ApiRequested()

data class ApiTopicAppendEventRequest(
    override val topicId: TopicId,
    override val payload: TopicEventPayload
) : TopicAppendEntryRequest

data class ApiTopicAppendRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val topicId: TopicId
) : ApiRequested()

data class ApiTopicEventList(
    val topicId: TopicId,
    val topicName: TopicName,
    val events: List<Event>
) {
    data class Event(
        val id: TopicEventId,
        val payload: TopicEventPayload
    )
}

data class ApiTopic(
    val id: TopicId,
    val name: TopicName
) : ApiObject()

data class ApiTopicList(
    val topics: List<Topic>
) : ApiObject() {
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}

interface ApiTopicService {
    fun append(topicId: TopicId, payload: TopicEventPayload): ApiTopicAppendRequested
    fun create(groupId: GroupId, req: ApiTopicCreateRequest): ApiTopicGroupCreateRequested
    fun list(query: TopicQuery): List<ApiTopicList.Topic>
    fun events(topicId: TopicId): List<ApiTopicEventList.Event>
    fun get(topicId: TopicId): ApiTopic
    fun resolve(namespaceId: NamespaceId, topicName: TopicName): TopicId

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var topicIds: List<TopicId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (topicIds.isNotEmpty()) req.parameter("topic_ids", topicIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiTopicServiceImpl(
    private val template: HttpTemplate
) : ApiTopicService {

    override fun append(topicId: TopicId, payload: TopicEventPayload): ApiTopicAppendRequested =
        template.post("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(ApiTopicAppendRequested::class)

    override fun create(groupId: GroupId, req: ApiTopicCreateRequest): ApiTopicGroupCreateRequested =
        template.post("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiTopicGroupCreateRequested::class)

    override fun list(query: TopicQuery) =
        template.get("/v1/topics")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiTopicList::class)
            .topics

    override fun events(topicId: TopicId) =
        template
            .get("/v1/topics/{topicId}/events")
            .path("topicId", topicId)
            .execute()
            .fold(ApiTopicEventList::class)
            .events

    override fun get(topicId: TopicId) =
        template.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()
            .fold(ApiTopic::class)

    override fun resolve(namespaceId: NamespaceId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/topics")
                .parameter("namespace_ids", namespaceId)
                .parameter("names", topicName.value)
                .execute()
                .fold(ApiTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}