package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.vo.TopicId
import java.io.Closeable


interface LogBrokerConsumersRepository : Closeable {
    fun nextChunkId(groupId: GroupId, topicId: TopicId): LogChunkId
    fun commit(groupId: GroupId, topicId: TopicId, chunkId: LogChunkId)
    fun count(): ULong
}