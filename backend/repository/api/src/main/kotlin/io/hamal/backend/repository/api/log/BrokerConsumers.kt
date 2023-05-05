package io.hamal.backend.repository.api.log

interface BrokerConsumersRepository : AutoCloseable {
    fun nextChunkId(groupId: Consumer.GroupId, topicId: Topic.Id): Chunk.Id
    fun commit(groupId: Consumer.GroupId, topicId: Topic.Id, chunkId: Chunk.Id)
}