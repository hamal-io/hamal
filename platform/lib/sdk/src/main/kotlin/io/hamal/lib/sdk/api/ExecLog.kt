package io.hamal.lib.sdk.api

import io.hamal.lib.domain.request.ExecLogAppendRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

data class ApiExecLogAppendRequest(
    override val level: ExecLogLevel,
    override val message: ExecLogMessage,
    override val timestamp: ExecLogTimestamp
) : ExecLogAppendRequest

data class ApiExcLogList(
    val logs: List<ApiExecLog>
) : ApiObject()

data class ApiExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val timestamp: ExecLogTimestamp
) : ApiObject()

interface ApiExecLogService {
    fun append(execId: ExecId, req: ApiExecLogAppendRequest)
}

internal class ApiExecLogServiceImpl(
    private val template: HttpTemplate
) : ApiExecLogService {
    override fun append(execId: ExecId, req: ApiExecLogAppendRequest) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(req)
            .execute()
    }
}