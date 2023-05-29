package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable
import java.nio.file.Path

data class LogTopic(
    val id: TopicId,
    val logBrokerId: LogBroker.Id,
    val name: TopicName,
    val path: Path,
    val shard: Shard
)

interface LogTopicRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}