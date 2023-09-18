package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.GroupId
import java.io.Closeable

data class Topic(
    val id: TopicId,
    val groupId: GroupId,
    val name: TopicName
)

interface TopicRepository : ChunkAppender, ChunkReader, ChunkCounter, Closeable {
    fun clear()
}

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)