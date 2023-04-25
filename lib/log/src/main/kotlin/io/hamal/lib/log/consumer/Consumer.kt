package io.hamal.lib.log.consumer

import io.hamal.lib.log.broker.BrokerRepository
import io.hamal.lib.log.topic.Topic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Consumer<VALUE : Any> {
    val groupId: GroupId

    fun consume(limit: Int, fn: (VALUE) -> Unit)

    @JvmInline
    value class GroupId(val value: String)
}


class ProtobufConsumer<Value : Any>(
    override val groupId: Consumer.GroupId,
    private val topic: Topic,
    private val brokerRepository: BrokerRepository,
    private val valueClass: KClass<Value>
) : Consumer<Value> {

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun consume(limit: Int, fn: (Value) -> Unit) {
        brokerRepository.read(groupId, topic, limit)
            .onEach { chunk ->
                val value = ProtoBuf.decodeFromByteArray(valueClass.serializer(), chunk.bytes)
                fn(value)
            }
            .map { it.id }
            .maxByOrNull { it }
            ?.let { brokerRepository.commit(groupId, topic, it) }
    }
}