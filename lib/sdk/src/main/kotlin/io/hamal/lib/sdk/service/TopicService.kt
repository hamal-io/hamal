package io.hamal.lib.sdk.service

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate

interface TopicService {
    fun resolve(topicName: TopicName): TopicId
}

class DefaultTopicService(
    val template: HttpTemplate,
    val topicNameCache: KeyedOnce<TopicName, TopicId> = KeyedOnce.default()
) : TopicService {
    override fun resolve(topicName: TopicName): TopicId {
        return topicNameCache(topicName) {
            TODO()
        }
    }

}