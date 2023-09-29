package io.hamal.lib.sdk.hub

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.request.AppendExecLogReq
import kotlinx.serialization.Serializable

@Serializable
data class HubAppendExecLogCmd(
    override val level: ExecLogLevel,
    override val message: ExecLogMessage,
    override val localAt: LocalAt
) : AppendExecLogReq


@Serializable
data class HubExcLogList(
    val logs: List<HubExecLog>
)


@Serializable
data class HubExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt,
    val remoteAt: RemoteAt
)


interface HubExecLogService {
    fun append(execId: ExecId, req: HubAppendExecLogCmd)
}

internal class DefaultHubExecLogService(
    private val template: HttpTemplate
) : HubExecLogService {
    override fun append(execId: ExecId, req: HubAppendExecLogCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(req)
            .execute()
    }
}