package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable

interface BrokerConsumersRepository : Closeable {
    fun nextChunkId(groupId: Consumer.GroupId, topicId: TopicId): Chunk.Id
    fun commit(groupId: Consumer.GroupId, topicId: TopicId, chunkId: Chunk.Id)
}