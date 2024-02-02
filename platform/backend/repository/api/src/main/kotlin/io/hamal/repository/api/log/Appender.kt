package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId

@Deprecated("")
interface Appender<VALUE : Any> {
    fun append(cmdId: CmdId, topic: DepTopic, value: VALUE)
}

class AppenderImpl<VALUE : Any>(
    private val repository: BrokerRepository
) : Appender<VALUE> {

    override fun append(cmdId: CmdId, topic: DepTopic, value: VALUE) {
        val encoded = json.serialize(value).toByteArray()
        repository.append(cmdId + encoded.contentHashCode(), topic, encoded)
    }
}
