package io.hamal.lib.sdk.admin

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.AppendEntryReq
import io.hamal.request.CreateTopicReq
import kotlinx.serialization.Serializable

@Serializable
data class AdminCreateTopicReq(
    override val name: TopicName
) : CreateTopicReq

@Serializable
data class AdminAppendEntryReq(
    override val topicId: TopicId,
    override val payload: TopicEntryPayload
) : AppendEntryReq

@Serializable
data class AdminTopicEntryList(
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
data class AdminTopic(
    val id: TopicId,
    val name: TopicName
)

@Serializable
data class AdminTopicList(
    val topics: List<Topic>
) {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


interface AdminTopicService {
    fun append(topicId: TopicId, payload: TopicEntryPayload): AdminSubmittedReqWithId
    fun create(groupId: GroupId, req: AdminCreateTopicReq): AdminSubmittedReqWithId
    fun list(groupIds: List<GroupId>): List<AdminTopicList.Topic>
    fun entries(topicId: TopicId): List<AdminTopicEntryList.Entry>
    fun get(topicId: TopicId): AdminTopic
    fun resolve(groupId: GroupId, topicName: TopicName): TopicId
}

internal class DefaultAdminTopicService(
    private val template: HttpTemplate
) : AdminTopicService {

    override fun append(topicId: TopicId, payload: TopicEntryPayload) =
        template.post("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .body(payload)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun create(groupId: GroupId, req: AdminCreateTopicReq) =
        template.post("/v1/groups/{groupId}/topics")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun list(groupIds: List<GroupId>) =
        template.get("/v1/groups/{groupId}/topics")
            .parameter("group_ids", groupIds)
            .execute()
            .fold(AdminTopicList::class)
            .topics

    override fun entries(topicId: TopicId) =
        template
            .get("/v1/topics/{topicId}/entries")
            .path("topicId", topicId)
            .execute()
            .fold(AdminTopicEntryList::class)
            .entries

    override fun get(topicId: TopicId) =
        template.get("/v1/topics/{topicId}")
            .path("topicId", topicId)
            .execute()
            .fold(AdminTopic::class)

    override fun resolve(groupId: GroupId, topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            template.get("/v1/groups/{groupId}/topics")
                .path("groupId", groupId)
                .parameter("names", topicName.value)
                .execute(AdminTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}