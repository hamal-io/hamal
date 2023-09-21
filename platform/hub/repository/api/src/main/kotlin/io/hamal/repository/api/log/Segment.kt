package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Repository

interface Segment {
    val id: Id
    val topicId: TopicId

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


interface SegmentRepository : Repository, ChunkAppender, ChunkReader, ChunkCounter