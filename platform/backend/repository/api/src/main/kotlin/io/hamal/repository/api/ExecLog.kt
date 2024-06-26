package io.hamal.repository.api

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecLogId.Companion.ExecLogId

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
    override val id: ExecLogId,
    val execId: ExecId,
    override val workspaceId: WorkspaceId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val timestamp: ExecLogTimestamp,
) : DomainObject<ExecLogId>, HasWorkspaceId {
    override val updatedAt: UpdatedAt = UpdatedAt(timestamp.value)
}
