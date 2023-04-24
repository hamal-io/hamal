package io.hamal.lib.log.consumer

import io.hamal.lib.log.broker.Broker
import io.hamal.lib.log.topic.Topic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Consumer<Key : Any, Value : Any> {

    fun poll(): List<Consumer.Record<Key, Value>>

    data class Record<Key : Any, Value : Any>(
        val key: Key,
        val value: Value
    )
}

class ProtobufConsumer<Key : Any, Value : Any>(

    private val topicId: Topic.Id,
    private val broker: Broker,
    private val keyClass: KClass<Key>,
    private val valueClass: KClass<Value>
) : Consumer<Key, Value> {

    private var offset: Long = 0

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun poll(): List<Consumer.Record<Key, Value>> {
        val topic = broker.getTopic(topicId)

        val result = topic.read(Topic.Record.Id(offset), limit = 1)
            .map { record ->
                Consumer.Record<Key, Value>(
                    key = ProtoBuf.decodeFromByteArray(keyClass.serializer(), record.key.array()),
                    value = ProtoBuf.decodeFromByteArray(valueClass.serializer(), record.value.array())
                )
            }

        offset += result.size

        return result
    }

}