package io.hamal.lib.log.consumer

import io.hamal.lib.log.broker.BrokerRepository
import io.hamal.lib.log.topic.Topic
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Consumer<VALUE : Any> {
    val groupId: GroupId

    fun consume(limit: Int, fn: (VALUE) -> Unit): Int {
        return consumeIndexed(limit) { _, value -> fn(value) }
    }

    fun consumeIndexed(limit: Int, fn: (Int, VALUE) -> Unit): Int

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
    override fun consumeIndexed(limit: Int, fn: (Int, Value) -> Unit): Int {
        val chunksToConsume = brokerRepository.read(groupId, topic, limit)

        chunksToConsume.onEachIndexed { index, chunk ->
            fn(
                index,
                ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes)
            )
        }
            .map { it.id }
            .maxByOrNull { it }
            ?.let { brokerRepository.commit(groupId, topic, it) }

        return chunksToConsume.size
    }
}