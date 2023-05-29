package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable
import java.nio.file.Path

data class LogShard(
    val id: Shard,
    val topicId: TopicId,
    val path: Path
)

interface LogShardRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}