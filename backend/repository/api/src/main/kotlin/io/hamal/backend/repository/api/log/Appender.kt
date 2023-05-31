package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.ReqId

interface Appender<VALUE : Any> {
    fun append(reqId: ReqId, topic: LogTopic, value: VALUE)
}