package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.request.AppendExecLogReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAppendExecLogCmd(
    override val level: ExecLogLevel,
    override val message: ExecLogMessage,
    override val localAt: LocalAt
) : AppendExecLogReq

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

interface ApiExecLogService {
    fun append(execId: ExecId, req: ApiAppendExecLogCmd)
}

internal class ApiExecLogServiceImpl(
    private val template: HttpTemplateImpl
) : ApiExecLogService {
    override fun append(execId: ExecId, req: ApiAppendExecLogCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(req)
            .execute()
    }
}