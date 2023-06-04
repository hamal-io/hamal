package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.CmdId

interface Appender<VALUE : Any> {
    fun append(cmdId: CmdId, topic: LogTopic, value: VALUE)
}