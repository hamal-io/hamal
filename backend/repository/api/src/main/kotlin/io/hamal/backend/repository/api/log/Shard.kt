package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Partition
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface LogPartition {
    val id: Partition
    val topicId: TopicId
}

interface LogPartitionRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}