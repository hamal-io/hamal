@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.backend.repository.api.log

import io.hamal.lib.common.domain.CmdId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Appender<TOPIC : LogTopic, VALUE : Any> {
    fun append(cmdId: CmdId, topic: TOPIC, value: VALUE)
}

class ProtobufAppender<TOPIC : LogTopic, VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val repository: LogBrokerRepository<TOPIC>
) : Appender<TOPIC, VALUE> {
    @OptIn(InternalSerializationApi::class)
    override fun append(cmdId: CmdId, topic: TOPIC, value: VALUE) {
        val encoded = ProtoBuf.encodeToByteArray(valueClass.serializer(), value)
        repository.append(cmdId, topic, encoded)
    }
}
