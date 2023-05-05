package io.hamal.backend.repository.api.log

import java.io.Closeable

interface BrokerConsumersRepository : Closeable {
    fun nextChunkId(groupId: Consumer.GroupId, topicId: Topic.Id): Chunk.Id
    fun commit(groupId: Consumer.GroupId, topicId: Topic.Id, chunkId: Chunk.Id)
}