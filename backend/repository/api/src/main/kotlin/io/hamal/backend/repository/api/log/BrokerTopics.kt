package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface BrokerTopicsRepository : Closeable {
    fun resolveTopic(name: TopicName): Topic
    fun find(topicId: TopicId): Topic?
}
