package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.LogTopicId

interface LogTopicAppender<VALUE : Any> {
    fun append(cmdId: CmdId, topicId: LogTopicId, value: VALUE)
}

class LogTopicAppenderImpl<VALUE : Any>(
    private val repository: LogBrokerRepository
) : LogTopicAppender<VALUE> {

    override fun append(cmdId: CmdId, topicId: LogTopicId, value: VALUE) {
        val encoded = serde.write(value).toByteArray()
        repository.append(cmdId, topicId, encoded)
    }
}
