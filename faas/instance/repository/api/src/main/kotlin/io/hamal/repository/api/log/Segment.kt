package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface LogSegment {
    val id: Id
    val topicId: TopicId

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface LogSegmentRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}