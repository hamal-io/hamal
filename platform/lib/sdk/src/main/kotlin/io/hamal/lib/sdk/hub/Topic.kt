package io.hamal.lib.sdk.hub

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.AppendEntryReq
import io.hamal.request.CreateTopicReq
import kotlinx.serialization.Serializable

@Serializable
data class HubCreateTopicReq(
    override val name: TopicName
) : CreateTopicReq

@Serializable
data class HubAppendEntryReq(
    override val topicId: TopicId,
    override val payload: TopicEntryPayload
) : AppendEntryReq

@Serializable
data class HubTopicEntryList(
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
data class HubTopic(
    val id: TopicId,
    val name: TopicName
)

@Serializable
data class HubTopicList(
    val topics: List<Topic>
) {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


interface HubTopicService {
    fun append(topicId: TopicId, payload: TopicEntryPayload): HubSubmittedReqWithId
    fun create(groupId: GroupId, req: HubCreateTopicReq): HubSubmittedReqWithId
    fun list(groupId: GroupId): List<HubTopicList.Topic>
    fun entries(topicId: TopicId): List<HubTopicEntryList.Entry>
    fun get(topicId: TopicId): HubTopic
    fun resolve(groupId: GroupId, topicName: TopicName): TopicId
}

internal class DefaultHubTopicService(
    private val template: HttpTemplate
) : HubTopicService {

    override fun append(topicId: TopicId, payload: TopicEntryPayload) =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun create(groupId: GroupId, req: HubCreateTopicReq) =
        template.post("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
            .execute()
            .fold(HubTopicList::class)
            .topics

    override fun entries(topicId: TopicId) =
        template
            .get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()
            .fold(HubTopicEntryList::class)
            .entries

    override fun get(topicId: TopicId) =
        template.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()
            .fold(HubTopic::class)

    override fun resolve(groupId: GroupId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/groups/{groupId}/topics")
                .path("groupId", groupId)
                .parameter("names", topicName.value)
                .execute(HubTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}