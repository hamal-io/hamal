@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.Topic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Appender<VALUE : Any> {
    fun append(topic: Topic, vararg values: VALUE)
}

class ProtobufAppender<VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val brokerRepository: BrokerRepository
) : Appender<VALUE> {
    @OptIn(InternalSerializationApi::class)
    override fun append(topic: Topic, vararg values: VALUE) {
        val encodedValues = values.map { ProtoBuf.encodeToByteArray(valueClass.serializer(), it) }.toTypedArray()
        brokerRepository.append(topic, *encodedValues)
    }
}

