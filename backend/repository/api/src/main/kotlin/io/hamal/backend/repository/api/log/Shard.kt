package io.hamal.backend.repository.api.log

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface LogShard {
    val id: Shard
    val topicId: TopicId
}

interface LogShardRepository : LogChunkAppender, LogChunkReader, LogChunkCounter, Closeable {
    fun clear()
}