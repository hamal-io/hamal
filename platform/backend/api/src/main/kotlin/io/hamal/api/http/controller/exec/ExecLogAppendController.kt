package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.exec_log.ExecLogAppendPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExecLogAppendRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecLogAppendController(
    private val retry: Retry,
    private val execLogAppend: ExecLogAppendPort
) {
    @PostMapping("/v1/execs/{execId}/logs")
    fun append(
        @PathVariable("execId") execId: ExecId,
        @RequestBody cmd: ApiExecLogAppendRequest
    ): ResponseEntity<Unit> {
        return retry {
            execLogAppend(execId, cmd).let {
                ResponseEntity.accepted().build()
            }
        }
    }
}

