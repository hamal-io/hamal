package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface LogBrokerTopicsRepository : Closeable {
    fun resolveTopic(name: TopicName): LogTopic
    fun find(topicId: TopicId): LogTopic?
}
