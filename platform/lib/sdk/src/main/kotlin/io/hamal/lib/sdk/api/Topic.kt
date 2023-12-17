package io.hamal.lib.sdk.api

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTopicService.TopicQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.TopicAppendEntryReq
import io.hamal.request.TopicCreateReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiTopicCreateReq(
    override val name: TopicName
) : TopicCreateReq

@Serializable
data class ApiTopicCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val topicId: TopicId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted


@Serializable
data class ApiTopicAppendEntryReq(
    override val topicId: TopicId,
    override val payload: TopicEntryPayload
) : TopicAppendEntryReq

@Serializable
data class ApiTopicAppendSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val topicId: TopicId
) : ApiSubmitted


@Serializable
data class ApiTopicEntryList(
    val topicId: TopicId,
    val topicName: TopicName,
    val entries: List<Entry>
) {
    @Serializable
    data class Entry(
        val id: TopicEntryId,
        val payload: TopicEntryPayload
    )
}

@Serializable
data class ApiTopic(
    val id: TopicId,
    val name: TopicName
)

@Serializable
data class ApiTopicList(
    val topics: List<Topic>
) {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}

interface ApiTopicService {
    fun append(topicId: TopicId, payload: TopicEntryPayload): ApiTopicAppendSubmitted
    fun create(flowId: FlowId, req: ApiTopicCreateReq): ApiTopicCreateSubmitted
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

    override fun append(topicId: TopicId, payload: TopicEntryPayload): ApiTopicAppendSubmitted =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(ApiTopicAppendSubmitted::class)

    override fun create(flowId: FlowId, req: ApiTopicCreateReq): ApiTopicCreateSubmitted =
        template.post("/v1/flows/{flowId}/topics")
            .path("flowId", flowId)
            .body(req)
            .execute()
            .fold(ApiTopicCreateSubmitted::class)

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