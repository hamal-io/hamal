@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Appender
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


class ProtobufAppender<VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val logBrokerRepository: LogBrokerRepository
) : Appender<VALUE> {
    @OptIn(InternalSerializationApi::class)
    override fun append(topic: LogTopic, value: VALUE) {
        val encoded = ProtoBuf.encodeToByteArray(valueClass.serializer(), value)
        logBrokerRepository.append(topic, encoded)
    }
}

