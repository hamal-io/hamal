package io.hamal.lib.log.producer

import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.broker.Broker
import io.hamal.lib.log.producer.Producer.Record
import io.hamal.lib.log.topic.Topic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import java.nio.ByteBuffer
import java.time.Instant
import kotlin.reflect.KClass

interface Producer<KEY : Any, VALUE : Any> {

    fun produce(topicId: Topic.Id, record: Record<KEY, VALUE>)

    data class Record<KEY : Any, VALUE : Any>(
        val key: KEY,
        val keyCLass: KClass<KEY>,
        val value: VALUE,
        val valueClass: KClass<VALUE>,
    )
}

class ProtobufProducer<KEY : Any, VALUE : Any>(
    private val broker: Broker
) : Producer<KEY, VALUE> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun produce(topicId: Topic.Id, record: Record<KEY, VALUE>) {
        broker.append(
            topicId, ToRecord(
                key = ByteBuffer.wrap(
                    ProtoBuf.encodeToByteArray(record.keyCLass.serializer(), record.key)
                ),
                value = ByteBuffer.wrap(
                    ProtoBuf.encodeToByteArray(record.valueClass.serializer(), record.value)
                ),
                instant = Instant.now()
            )
        )
    }
}

