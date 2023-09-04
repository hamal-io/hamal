package io.hamal.lib.sdk.hub

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

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
    fun resolve(topicName: TopicName): TopicId
}

internal class DefaultHubTopicService(
    private val httpTemplate: HttpTemplate
) : HubTopicService {

    override fun resolve(topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            httpTemplate.get("/v1/topics")
                .parameter("names", topicName.value)
                .execute(HubTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}