package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

data class LogTopic(
    val id: TopicId,
    val name: TopicName
)

interface LogTopicRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}