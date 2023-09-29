package io.hamal.admin.web.exec

import io.hamal.core.adapter.ListExecLogsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminExcLogList
import io.hamal.lib.sdk.admin.AdminExecLog
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListExecsLogsController(private val execsLogs: ListExecLogsPort) {
    @GetMapping("/v1/exec-logs")
    fun getExecLogs(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>,
        @RequestParam(required = false, name = "exec_ids", defaultValue = "") execIds: List<ExecId>,
        @RequestParam(required = false, name = "exec_log_ids", defaultValue = "") execLogIds: List<ExecLogId>
    ): ResponseEntity<AdminExcLogList> {
        return execsLogs(
            ExecLogQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds,
                execIds = execIds,
                execLogIds = execLogIds
            )
        ) { logs ->
            ResponseEntity.ok(
                AdminExcLogList(
                    logs.map {
                        AdminExecLog(
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
}