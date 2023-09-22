package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Repository

data class Topic(
    val id: TopicId,
    val groupId: GroupId,
    val name: TopicName
)

interface TopicRepository : Repository, ChunkAppender, ChunkReader, ChunkCounter

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)