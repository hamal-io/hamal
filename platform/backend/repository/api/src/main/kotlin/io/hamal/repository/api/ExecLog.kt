package io.hamal.repository.api

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*

interface ExecLogRepository : ExecLogCmdRepository, ExecLogQueryRepository

interface ExecLogCmdRepository : CmdRepository {
    fun append(cmd: AppendCmd): ExecLog
    data class AppendCmd(
        val execLogId: ExecLogId,
        val level: ExecLogLevel,
        val execId: ExecId,
        val workspaceId: WorkspaceId,
        val message: ExecLogMessage,
        val timestamp: ExecLogTimestamp
    )
}

interface ExecLogQueryRepository {
    fun list(query: ExecLogQuery): List<ExecLog>
    fun count(query: ExecLogQuery): Count

    data class ExecLogQuery(
        var afterId: ExecLogId = ExecLogId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var execLogIds: List<ExecLogId> = listOf(),
        var execIds: List<ExecId> = listOf(),
        var workspaceIds: List<WorkspaceId>
    )
}

data class ExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    override val workspaceId: WorkspaceId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val timestamp: ExecLogTimestamp,
) : HasWorkspaceId
