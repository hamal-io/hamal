package io.hamal.lib.sdk.api

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTopicService.TopicQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.AppendEntryReq
import io.hamal.request.CreateTopicReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateTopicReq(
    override val name: TopicName
) : CreateTopicReq

@Serializable
data class ApiAppendEntryReq(
    override val topicId: TopicId,
    override val payload: TopicEntryPayload
) : AppendEntryReq

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
    fun append(topicId: TopicId, payload: TopicEntryPayload): ApiSubmittedReqWithId
    fun create(namespaceId: NamespaceId, req: ApiCreateTopicReq): ApiSubmittedReqWithId
    fun list(query: TopicQuery): List<ApiTopicList.Topic>
    fun entries(topicId: TopicId): List<ApiTopicEntryList.Entry>
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
    private val template: HttpTemplateImpl
) : ApiTopicService {

    override fun append(topicId: TopicId, payload: TopicEntryPayload) =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun create(namespaceId: NamespaceId, req: ApiCreateTopicReq) =
        template.post("/v1/namespaces/{namespaceId}/topics")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

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

    override fun resolve(namespaceId: NamespaceId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/topics")
                .parameter("namespace_ids", namespaceId)
                .parameter("names", topicName.value)
                .execute(ApiTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}