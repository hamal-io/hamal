package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CmdRepository

data class Topic(
    val id: TopicId,
    val namespaceId: NamespaceId,
    val groupId: GroupId,
    val name: TopicName
)

interface TopicRepository : CmdRepository, ChunkAppender, ChunkReader, ChunkCounter

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)