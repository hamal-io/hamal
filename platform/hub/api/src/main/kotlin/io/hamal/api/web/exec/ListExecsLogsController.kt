package io.hamal.api.web.exec

import io.hamal.core.component.exec.ListExecsLogs
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubExcLogList
import io.hamal.lib.sdk.hub.HubExecLog
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListExecsLogsController(private val execsLogs: ListExecsLogs) {
    @GetMapping("/v1/groups/{groupId}/exec-logs")
    fun getExecLogs(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecLogId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubExcLogList> {
        return execsLogs(
            ExecLogQuery(
                afterId = afterId,
                limit = limit,
                // group id
            )
        ) { logs ->
            ResponseEntity.ok(
                HubExcLogList(
                    logs.map {
                        HubExecLog(
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