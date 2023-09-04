package io.hamal.lib.sdk.hub

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
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


interface ExecLogService {
    fun append(execId: ExecId, cmd: AppendExecLogCmd)
}

internal class DefaultExecLogService(
    private val template: HttpTemplate
) : ExecLogService {
    override fun append(execId: ExecId, cmd: AppendExecLogCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(cmd)
            .execute()
    }
}