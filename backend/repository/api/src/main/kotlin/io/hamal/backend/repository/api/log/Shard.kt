package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable
import java.nio.file.Path

data class LogShard(
    val id: Id,
    val topicId: TopicId,
    val path: Path,
    val shard: Shard
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

interface LogShardRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}