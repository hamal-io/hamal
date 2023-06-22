package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface LogSegment {
    val id: Id
    val shard: Shard
    val topicId: TopicId
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface LogSegmentRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}