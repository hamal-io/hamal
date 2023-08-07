package io.hamal.backend.instance.web.exec

import io.hamal.backend.repository.api.ExecLogQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.sdk.domain.ListExecLogsResponse
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
        @RequestParam(required = false, name = "after_id", defaultValue = "${Long.MAX_VALUE}") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListExecLogsResponse> {
        val logs = execLogQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ListExecLogsResponse(logs)
        )
    }
}