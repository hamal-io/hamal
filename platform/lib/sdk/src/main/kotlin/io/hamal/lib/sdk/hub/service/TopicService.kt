package io.hamal.lib.sdk.hub.service

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.hub.domain.ApiTopicList

interface TopicService {
    fun resolve(topicName: TopicName): TopicId
}

internal class DefaultTopicService(
    private val httpTemplate: HttpTemplate
) : TopicService {

    override fun resolve(topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            httpTemplate.get("/v1/topics")
                .parameter("names", topicName.value)
                .execute(ApiTopicList::class)
                .topics
                .firstOrNull()?.id
                ?: throw NoSuchElementException("Topic not found")
        }
    }

    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
}