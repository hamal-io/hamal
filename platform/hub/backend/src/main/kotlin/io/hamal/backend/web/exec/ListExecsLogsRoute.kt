package io.hamal.backend.web.exec

import io.hamal.repository.api.ExecLogQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.sdk.hub.ApiExcLogList
import io.hamal.lib.sdk.hub.ApiExecLog
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListExecsLogsRoute(
    private val execLogQueryRepository: ExecLogQueryRepository
) {
    @GetMapping("/v1/exec-logs")
    fun getExecLogs(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiExcLogList> {
        val logs = execLogQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ApiExcLogList(
                logs.map {
                    ApiExecLog(
                        id = it.id,
                        execId = it.execId,
                        level = it.level,
                        message = it.message,
                        localAt = it.localAt,
                        remoteAt = it.remoteAt
                    )
                }
            )
        )
    }
}