package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CmdRepository

data class DepTopic(
    val id: TopicId,
    val flowId: FlowId,
    val groupId: GroupId,
    val name: TopicName
)

@Deprecated("")
interface DepTopicRepository : CmdRepository, ChunkAppender, ChunkReader, ChunkCounter

data class TopicEntry(
    val id: TopicEntryId,
    val payload: TopicEntryPayload
)