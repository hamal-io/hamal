package io.hamal.backend.repository.api.log

import io.hamal.lib.core.Shard
import java.io.Closeable
import java.nio.file.Path

data class Topic(
    val id: Id,
    val brokerId: Broker.Id,
    val name: Name,
    val path: Path,
    val shard: Shard
) {

    @JvmInline
    value class Name(val value: String)

    @JvmInline
    value class Id(val value: Int)
}

interface TopicRepository : ChunkAppender, ChunkReader, ChunkCounter, Closeable {
    fun clear()
}