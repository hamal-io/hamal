package io.hamal.lib.sdk.domain

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class AppendExecLogCmd(
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt
)


@Serializable
data class ApiExcLogList(
    val logs: List<ApiExecLog>
)


@Serializable
data class ApiExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt,
    val remoteAt: RemoteAt
)
