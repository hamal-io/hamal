package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.CommandId

interface Appender<VALUE : Any> {
    fun append(commandId: CommandId, topic: LogTopic, value: VALUE)
}