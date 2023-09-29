package io.hamal.lib.sdk.admin

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.request.AppendExecLogReq
import kotlinx.serialization.Serializable

@Serializable
data class AdminAppendExecLogCmd(
    override val level: ExecLogLevel,
    override val message: ExecLogMessage,
    override val localAt: LocalAt
) : AppendExecLogReq


@Serializable
data class AdminExcLogList(
    val logs: List<AdminExecLog>
)


@Serializable
data class AdminExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt,
    val remoteAt: RemoteAt
)


interface AdminExecLogService {
    fun append(execId: ExecId, cmd: AdminAppendExecLogCmd)
}

internal class DefaultAdminExecLogService(
    private val template: HttpTemplate
) : AdminExecLogService {
    override fun append(execId: ExecId, cmd: AdminAppendExecLogCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(cmd)
            .execute()
    }
}