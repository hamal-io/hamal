package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.CmdRepository

data class DepTopic(
    val id: TopicId,
    val flowId: FlowId,
    val groupId: GroupId,
    val name: TopicName
)

@Deprecated("")
interface DepTopicRepository : CmdRepository, ChunkAppender, ChunkReader, ChunkCounter

