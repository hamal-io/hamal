package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp

interface ExecLogAppendRequest {
    val level: ExecLogLevel
    val message: ExecLogMessage
    val timestamp: ExecLogTimestamp
}