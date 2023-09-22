package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Repository


interface BrokerConsumersRepository : Repository {
    fun nextChunkId(consumerId: ConsumerId, topicId: TopicId): ChunkId
    fun commit(consumerId: ConsumerId, topicId: TopicId, chunkId: ChunkId)
    fun count(): ULong
}