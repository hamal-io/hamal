package io.hamal.lib.sdk.api

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
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
    fun create(groupId: GroupId, req: ApiCreateTopicReq): ApiSubmittedReqWithId
    fun list(groupId: GroupId): List<ApiTopicList.Topic>
    fun entries(topicId: TopicId): List<ApiTopicEntryList.Entry>
    fun get(topicId: TopicId): ApiTopic
    fun resolve(groupId: GroupId, topicName: TopicName): TopicId
}

internal class ApiTopicServiceImpl(
    private val template: HttpTemplate
) : ApiTopicService {

    override fun append(topicId: TopicId, payload: TopicEntryPayload) =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun create(groupId: GroupId, req: ApiCreateTopicReq) =
        template.post("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
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

    override fun resolve(groupId: GroupId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/groups/{groupId}/topics")
                .path("groupId", groupId)
                .parameter("names", topicName.value)
                .execute(ApiTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}