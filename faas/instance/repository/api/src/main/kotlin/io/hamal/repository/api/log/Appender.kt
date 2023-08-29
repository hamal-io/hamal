@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface Appender<VALUE : Any> {
    fun append(cmdId: CmdId, topic: LogTopic, value: VALUE)
}

class ProtobufAppender<VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val repository: LogBrokerRepository
) : Appender<VALUE> {
    @OptIn(InternalSerializationApi::class)
    override fun append(cmdId: CmdId, topic: LogTopic, value: VALUE) {
        val encoded = ProtoBuf.encodeToByteArray(valueClass.serializer(), value)
        repository.append(cmdId + encoded.contentHashCode(), topic, encoded)
    }
}
