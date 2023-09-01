package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

data class Topic(
    val id: TopicId,
    val name: TopicName
)

interface TopicRepository : ChunkAppender, ChunkReader, ChunkCounter, Closeable {
    fun clear()
}

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)