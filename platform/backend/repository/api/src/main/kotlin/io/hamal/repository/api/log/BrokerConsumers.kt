package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.CmdRepository

@Deprecated("")
interface BrokerConsumersRepository : CmdRepository {
    fun nextChunkId(consumerId: ConsumerId, topicId: TopicId): ChunkId
    fun commit(consumerId: ConsumerId, topicId: TopicId, chunkId: ChunkId)
    fun count(): ULong
}