package io.hamal.lib.log.consumer

import io.hamal.lib.log.broker.BrokerRepository
import io.hamal.lib.log.topic.Topic
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

interface Consumer<VALUE : Any> {
    val groupId: GroupId

    fun consume(limit: Int, fn: (VALUE) -> CompletableFuture<*>): Int {
        return consumeIndexed(limit) { _, value -> fn(value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, VALUE) -> CompletableFuture<*>): Int

    @JvmInline
    value class GroupId(val value: String)
}


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