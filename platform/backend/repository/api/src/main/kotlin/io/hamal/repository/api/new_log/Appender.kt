package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId

interface LogTopicAppender<VALUE : Any> {
    fun append(cmdId: CmdId, topicId: LogTopicId, value: VALUE)
}

class LogTopicAppenderImpl<VALUE : Any>(
    private val repository: LogBrokerRepository
) : LogTopicAppender<VALUE> {

    override fun append(cmdId: CmdId, topicId: LogTopicId, value: VALUE) {
        val encoded = json.serialize(value).toByteArray()
        repository.append(cmdId + encoded.contentHashCode(), topicId, encoded)
    }
}
