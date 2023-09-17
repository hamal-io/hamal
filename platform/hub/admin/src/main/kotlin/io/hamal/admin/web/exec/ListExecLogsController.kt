package io.hamal.admin.web.exec

import io.hamal.core.adapter.ListExecLogsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.sdk.admin.AdminExcLogList
import io.hamal.lib.sdk.admin.AdminExecLog
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListExecLogsController(
    private val execLogs: ListExecLogsPort
) {
    @GetMapping("/v1/execs/{execId}/logs")
    fun getExecLogs(
        @PathVariable("execId") execId: ExecId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminExcLogList> {
        return execLogs(
            execId, ExecLogQuery(
                afterId = afterId,
                limit = limit
            )
        ) { logs ->
            ResponseEntity.ok(
                AdminExcLogList(logs.map {
                    AdminExecLog(
                        id = it.id,
                        execId = it.execId,
                        level = it.level,
                        message = it.message,
                        localAt = it.localAt,
                        remoteAt = it.remoteAt
                    )
                })
            )
        }
    }
}