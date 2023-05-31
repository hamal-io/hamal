package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.ComputeId

interface Appender<VALUE : Any> {
    fun append(computeId: ComputeId, topic: LogTopic, value: VALUE)
}