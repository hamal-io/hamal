package io.hamal.backend.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

interface ExecLogCmdRepository {
    fun append(cmd: LogCmd): ExecLog
    fun clear()
    data class LogCmd(
        val id: ExecLogId,
        val level: ExecLogLevel,
        val execId: ExecId,
        val message: ExecLogMessage,
        val localAt: LocalAt
    )
}

interface ExecLogQueryRepository {
    fun list(execId: ExecId, block: ExecLogQuery.() -> Unit): List<ExecLog>

    fun list(block: ExecLogQuery.() -> Unit): List<ExecLog>

    data class ExecLogQuery(
        var afterId: ExecLogId = ExecLogId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}

@Serializable
data class ExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt,
    val remoteAt: RemoteAt
)
