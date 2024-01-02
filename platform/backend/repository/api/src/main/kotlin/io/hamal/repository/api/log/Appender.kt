package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import kotlin.reflect.KClass

interface Appender<VALUE : Any> {
    fun append(cmdId: CmdId, topic: Topic, value: VALUE)
}

class AppenderImpl<VALUE : Any>(
    private val valueClass: KClass<VALUE>,
    private val repository: BrokerRepository
) : Appender<VALUE> {

    override fun append(cmdId: CmdId, topic: Topic, value: VALUE) {
//        val encoded = ProtoBuf.encodeToByteArray(valueClass.serializer(), value)
//        repository.append(cmdId + encoded.contentHashCode(), topic, encoded)
        TODO()
    }
}
