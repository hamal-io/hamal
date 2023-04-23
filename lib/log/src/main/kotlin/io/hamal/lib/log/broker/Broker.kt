package io.hamal.lib.log.broker

import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.producer.Producer
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.meta.KeyedOnce
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.serializer
import java.nio.ByteBuffer
import java.time.Instant
import kotlin.io.path.Path

interface Broker {
    fun <KEY : Any, VALUE : Any> append(topicId: Topic.Id, record: Producer.Record<KEY, VALUE>)

    //FIXME remove from BROKER just for prototyping
    fun getTopic(topicId: Topic.Id): Topic

}

class DefaultBroker : Broker {


    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override fun <KEY : Any, VALUE : Any> append(topicId: Topic.Id, record: Producer.Record<KEY, VALUE>) {
        val topic = getTopic(topicId)

        topic.append(
            ToRecord(
                key = ByteBuffer.wrap("".toByteArray()),
                value = ByteBuffer.wrap(
                    Cbor.encodeToByteArray(record.valueClass.serializer(), record.value)
                ),
                instant = Instant.now()
            )
        )
    }

    override fun getTopic(topicId: Topic.Id): Topic = topicMapping.invoke(topicId) {
        Topic.open(
            Topic.Config(
                topicId,
                Path("/tmp/hamal/topics")
            )
        )
    }

    private val topicMapping = KeyedOnce.default<Topic.Id, Topic>()
}