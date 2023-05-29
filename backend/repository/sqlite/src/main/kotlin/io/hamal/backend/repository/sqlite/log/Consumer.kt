package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass


@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class ProtobufLogConsumer<Value : Any>(
    override val groupId: GroupId,
    private val topic: LogTopic,
    private val logBrokerRepository: LogBrokerRepository,
    private val valueClass: KClass<Value>
) : LogConsumer<Value> {

    override fun consumeIndexed(limit: Int, fn: (Int, Value) -> CompletableFuture<*>): Int {
        val chunksToConsume = logBrokerRepository.consume(groupId, topic, limit)

        chunksToConsume.mapIndexed { index, chunk ->
            val future = fn(
                index,
                ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes)
            )
            Pair(chunk.id, future)
        }
            .onEach { it.second.join() }
            .map { it.first }
            .maxByOrNull { it }
            ?.let { logBrokerRepository.commit(groupId, topic, it) }
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

    override fun consumeBatch(block: (List<Value>) -> CompletableFuture<*>): Int {
        val chunksToConsume = logBrokerRepository.consume(groupId, topic, 1)

        if (chunksToConsume.isEmpty()) {
            return 0
        }

        val batch = chunksToConsume
            .map { chunk -> ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes) }
            .also { block(it).join() }

        chunksToConsume.maxByOrNull { chunk -> chunk.id }?.let { logBrokerRepository.commit(groupId, topic, it.id) }
        return batch.size
    }
}