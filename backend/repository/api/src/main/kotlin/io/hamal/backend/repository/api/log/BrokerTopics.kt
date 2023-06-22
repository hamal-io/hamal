package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface LogBrokerTopicsRepository<TOPIC : LogTopic> : Closeable {
    fun resolveTopic(name: TopicName): TOPIC
    fun find(topicId: TopicId): TOPIC?
}
