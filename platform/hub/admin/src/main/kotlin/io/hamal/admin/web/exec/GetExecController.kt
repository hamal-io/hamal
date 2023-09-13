package io.hamal.admin.web.exec

import io.hamal.core.component.exec.GetExec
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.admin.AdminExec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetExecController(private val getExec: GetExec) {
    @GetMapping("/v1/execs/{execId}")
    fun get(@PathVariable("execId") execId: ExecId): ResponseEntity<AdminExec> {
        return getExec(execId) { exec ->
            ResponseEntity.ok(
                AdminExec(
                    id = exec.id,
                    status = exec.status,
                    correlation = exec.correlation,
                    inputs = exec.inputs,
                    code = exec.code,
                    events = exec.events
                )
            )
        }
    }
}