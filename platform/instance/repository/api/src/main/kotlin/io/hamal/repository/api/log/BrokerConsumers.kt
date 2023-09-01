package io.hamal.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable


interface BrokerConsumersRepository : Closeable {
    fun nextChunkId(groupId: GroupId, topicId: TopicId): ChunkId
    fun commit(groupId: GroupId, topicId: TopicId, chunkId: ChunkId)
    fun count(): ULong
}