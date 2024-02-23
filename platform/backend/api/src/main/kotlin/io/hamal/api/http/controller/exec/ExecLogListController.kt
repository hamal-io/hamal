package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.exec_log.ExecLogListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiExcLogList
import io.hamal.lib.sdk.api.ApiExecLog
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecLogListController(
    private val execLogList: ExecLogListPort
) {
    @GetMapping("/v1/execs/{execId}/logs")
    fun getExecLogs(
        @PathVariable("execId") execId: ExecId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiExcLogList> {
        return execLogList(
            ExecLogQuery(
                afterId = afterId,
                limit = limit,
                execIds = listOf(execId),
                workspaceIds = listOf()
            )
        ).let { logs ->
            ResponseEntity.ok(
                ApiExcLogList(logs.map {
                    ApiExecLog(
                        id = it.id,
                        execId = it.execId,
                        level = it.level,
                        message = it.message,
                        timestamp = it.timestamp
                    )
                })
            )
        }
    }

    @GetMapping("/v1/workspaces/{workspaceId}/exec-logs")
    fun getExecLogs(
        @PathVariable("workspaceId") workspaceId: WorkspaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiExcLogList> {
        return execLogList(
            ExecLogQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = listOf(workspaceId)
            )
        ).let { logs ->
            ResponseEntity.ok(
                ApiExcLogList(
                    logs.map {
                        ApiExecLog(
                            id = it.id,
                            execId = it.execId,
                            level = it.level,
                            message = it.message,
                            timestamp = it.timestamp
                        )
                    }
                )
            )
        }
    }
}