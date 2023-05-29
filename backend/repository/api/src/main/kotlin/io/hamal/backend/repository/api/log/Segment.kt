package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable
import java.nio.file.Path

data class LogSegment(
    val id: Id,
    val shard: Shard,
    val topicId: TopicId,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface LogSegmentRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}