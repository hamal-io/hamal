package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.CmdRepository

interface Segment {
    val id: Id
    val topicId: TopicId

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


@Deprecated("")
interface SegmentRepository : CmdRepository, ChunkAppender, ChunkReader, ChunkCounter