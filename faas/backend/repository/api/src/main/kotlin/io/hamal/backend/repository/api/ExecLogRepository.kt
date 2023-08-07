package io.hamal.backend.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.ExecLog
import io.hamal.lib.domain.ExecLogLevel
import io.hamal.lib.domain.LocalAt
import io.hamal.lib.domain.ExecLogMessage
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId

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
    data class ExecLogQuery(
        var afterId: ExecLogId = ExecLogId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}