package io.hamal.lib.sdk.api

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTopicService.TopicQuery
import io.hamal.lib.sdk.fold

data class ApiTopicCreateRequest(
    override val name: TopicName,
    override val type: TopicType
) : TopicCreateRequest

data class ApiTopicCreateRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: TopicId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val type: TopicType
) : ApiRequested()


data class ApiTopicAppendEventRequest(
    override val topicId: TopicId,
    override val payload: TopicEventPayload
) : TopicAppendEntryRequest

data class ApiTopicAppendRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: TopicId
) : ApiRequested()

data class ApiTopicEventList(
    val id: TopicId,
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
    val name: TopicName,
    val type: TopicType
) : ApiObject()

data class ApiTopicList(
    val topics: List<Topic>
) : ApiObject() {
    data class Topic(
        val id: TopicId,
        val name: TopicName,
        val type: TopicType
    )
}

interface ApiTopicService {
    fun append(topicId: TopicId, payload: TopicEventPayload): ApiTopicAppendRequested
    fun createTopic(namespaceId: NamespaceId, req: ApiTopicCreateRequest): ApiTopicCreateRequested
    fun list(query: TopicQuery): List<ApiTopicList.Topic>
    fun events(topicId: TopicId): List<ApiTopicEventList.Event>
    fun get(topicId: TopicId): ApiTopic
    fun resolve(namespaceId: NamespaceId, topicName: TopicName): TopicId

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var topicIds: List<TopicId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (topicIds.isNotEmpty()) req.parameter("topic_ids", topicIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (workspaceIds.isNotEmpty()) req.parameter("workspace_ids", workspaceIds)
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

    override fun createTopic(namespaceId: NamespaceId, req: ApiTopicCreateRequest): ApiTopicCreateRequested =
        template.post("/v1/namespaces/{namespaceId}/topics")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()
            .fold(ApiTopicCreateRequested::class)

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
                .parameter("names", topicName)
                .execute()
                .fold(ApiTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}