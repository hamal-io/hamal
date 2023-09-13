package io.hamal.request

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.LocalAt

interface AppendExecLogCmd {
    val level: ExecLogLevel
    val message: ExecLogMessage
    val localAt: LocalAt
}
