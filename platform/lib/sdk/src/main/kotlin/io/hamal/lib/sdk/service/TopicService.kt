package io.hamal.lib.sdk.service

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiTopicList

interface TopicService {
    fun resolve(topicName: TopicName): TopicId
}

class DefaultTopicService(
    val httpTemplate: HttpTemplate,
    private val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
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
}