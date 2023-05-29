package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface LogBrokerConsumersRepository : Closeable {
    fun nextChunkId(groupId: GroupId, topicId: TopicId): LogChunk.Id
    fun commit(groupId: GroupId, topicId: TopicId, chunkId: LogChunk.Id)
}