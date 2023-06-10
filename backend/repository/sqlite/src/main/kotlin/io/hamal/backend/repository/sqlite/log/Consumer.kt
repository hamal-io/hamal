package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class ProtobufLogConsumer<Value : Any>(
    override val groupId: GroupId,
    private val topic: LogTopic,
    private val logBrokerRepository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumer<Value> {

    override fun consumeIndexed(limit: Int, fn: (Int, LogChunkId, Value) -> Unit): Int {
        val chunksToConsume = logBrokerRepository.consume(groupId, topic, limit)

        chunksToConsume.mapIndexed { index, chunk ->
            fn(
                index,
                chunk.id,
                ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes)
            )
            chunk.id
        }.maxByOrNull { it }?.let { logBrokerRepository.commit(groupId, topic, it) }
        return chunksToConsume.size
    }
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class ProtobufBatchConsumer<Value : Any>(
    override val groupId: GroupId,
    private val topic: LogTopic,
    private val logBrokerRepository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : BatchConsumer<Value> {

    override fun consumeBatch(block: (List<Value>) -> Unit): Int {
        val chunksToConsume = logBrokerRepository.consume(groupId, topic, 1)

        if (chunksToConsume.isEmpty()) {
            return 0
        }

        val batch = chunksToConsume
            .map { chunk -> ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes) }

        chunksToConsume.maxByOrNull { chunk -> chunk.id }?.let { logBrokerRepository.commit(groupId, topic, it.id) }
        return batch.size
    }
}