package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.ExecLog
import io.hamal.lib.domain.ExecLogLevel
import io.hamal.lib.domain.LocalAt
import io.hamal.lib.domain.ExecLogMessage
import kotlinx.serialization.Serializable

@Serializable
data class AppendExecLogCmd(
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt
)


@Serializable
data class ListExecLogsResponse(
    val logs: List<ExecLog>
)


