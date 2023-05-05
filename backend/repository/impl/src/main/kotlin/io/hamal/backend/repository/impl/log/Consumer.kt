package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.Consumer
import io.hamal.backend.repository.api.log.Topic
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass


class ProtobufConsumer<Value : Any>(
    override val groupId: Consumer.GroupId,
    private val topic: Topic,
    private val brokerRepository: BrokerRepository,
    private val valueClass: KClass<Value>
) : Consumer<Value> {

    @OptIn(InternalSerializationApi::class)
    override fun consumeIndexed(limit: Int, fn: (Int, Value) -> CompletableFuture<*>): Int {
        val chunksToConsume = brokerRepository.read(groupId, topic, limit)

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
            ?.let { brokerRepository.commit(groupId, topic, it) }

        return chunksToConsume.size
    }
}