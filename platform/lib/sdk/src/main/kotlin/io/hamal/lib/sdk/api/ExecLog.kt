package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.request.ExecLogAppendReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiExecLogAppendCmd(
    override val level: ExecLogLevel,
    override val message: ExecLogMessage,
    override val timestamp: ExecLogTimestamp
) : ExecLogAppendReq

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
    val timestamp: ExecLogTimestamp,
)

interface ApiExecLogService {
    fun append(execId: ExecId, req: ApiExecLogAppendCmd)
}

internal class ApiExecLogServiceImpl(
    private val template: HttpTemplate
) : ApiExecLogService {
    override fun append(execId: ExecId, req: ApiExecLogAppendCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(req)
            .execute()
    }
}