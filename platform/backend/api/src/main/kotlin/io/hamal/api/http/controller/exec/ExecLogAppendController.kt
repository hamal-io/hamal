package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.ExecLogAppendPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExecLogAppendCmd
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecLogAppendController(
    private val retry: Retry,
    private val appendExecLog: ExecLogAppendPort
) {
    @PostMapping("/v1/execs/{execId}/logs")
    fun appendExecLog(
        @PathVariable("execId") execId: ExecId,
        @RequestBody cmd: ApiExecLogAppendCmd
    ): ResponseEntity<Unit> {
        return retry {
            appendExecLog(execId, cmd) {
                ResponseEntity.accepted().build()
            }
        }
    }
}

