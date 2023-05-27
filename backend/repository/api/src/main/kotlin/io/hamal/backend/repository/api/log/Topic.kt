package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable
import java.nio.file.Path

data class Topic(
    val id: TopicId,
    val brokerId: Broker.Id,
    val name: TopicName,
    val path: Path,
    val shard: Shard
)

interface TopicRepository : ChunkAppender, ChunkReader, ChunkCounter, Closeable {
    fun clear()
}