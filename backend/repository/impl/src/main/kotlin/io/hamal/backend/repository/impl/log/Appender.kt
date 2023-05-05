@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.Appender
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


class ProtobufAppender<VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val brokerRepository: BrokerRepository
) : Appender<VALUE> {
    @OptIn(InternalSerializationApi::class)
    override fun append(topic: Topic, value: VALUE) {
        val encoded = ProtoBuf.encodeToByteArray(valueClass.serializer(), value)
        brokerRepository.append(topic, encoded)
    }
}

