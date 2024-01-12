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

data class ApiTopicCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val topicId: TopicId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiRequested()

data class ApiTopicAppendEntryRequest(
    override val topicId: TopicId,
    override val payload: TopicEntryPayload
) : TopicAppendEntryRequest

data class ApiTopicAppendRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val topicId: TopicId
) : ApiRequested()

data class ApiTopicEntryList(
    val topicId: TopicId,
    val topicName: TopicName,
    val entries: List<Entry>
) {
    data class Entry(
        val id: TopicEntryId,
        val payload: TopicEntryPayload
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
    fun append(topicId: TopicId, payload: TopicEntryPayload): ApiTopicAppendRequested
    fun create(flowId: FlowId, req: ApiTopicCreateRequest): ApiTopicCreateRequested
    fun list(query: TopicQuery): List<ApiTopicList.Topic>
    fun entries(topicId: TopicId): List<ApiTopicEntryList.Entry>
    fun get(topicId: TopicId): ApiTopic
    fun resolve(flowId: FlowId, topicName: TopicName): TopicId

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var topicIds: List<TopicId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (topicIds.isNotEmpty()) req.parameter("topic_ids", topicIds)
            if (flowIds.isNotEmpty()) req.parameter("flow_ids", flowIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiTopicServiceImpl(
    private val template: HttpTemplate
) : ApiTopicService {

    override fun append(topicId: TopicId, payload: TopicEntryPayload): ApiTopicAppendRequested =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(ApiTopicAppendRequested::class)

    override fun create(flowId: FlowId, req: ApiTopicCreateRequest): ApiTopicCreateRequested =
        template.post("/v1/flows/{flowId}/topics")
            .path("flowId", flowId)
            .body(req)
            .execute()
            .fold(ApiTopicCreateRequested::class)

    override fun list(query: TopicQuery) =
        template.get("/v1/topics")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiTopicList::class)
            .topics

    override fun entries(topicId: TopicId) =
        template
            .get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()
            .fold(ApiTopicEntryList::class)
            .entries

    override fun get(topicId: TopicId) =
        template.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()
            .fold(ApiTopic::class)

    override fun resolve(flowId: FlowId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/topics")
                .parameter("flow_ids", flowId)
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