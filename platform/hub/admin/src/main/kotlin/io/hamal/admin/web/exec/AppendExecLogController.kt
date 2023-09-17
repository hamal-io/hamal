package io.hamal.admin.web.exec

import io.hamal.core.adapter.AppendExecLogPort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.admin.AdminAppendExecLogCmd
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AppendExecLogController(private val appendExecLog: AppendExecLogPort) {
    @PostMapping("/v1/execs/{execId}/logs")
    fun appendExecLog(
        @PathVariable("execId") execId: ExecId,
        @RequestBody cmd: AdminAppendExecLogCmd
    ): ResponseEntity<Unit> {
        return appendExecLog(execId, cmd) {
            ResponseEntity.accepted().build()
        }
    }
}

