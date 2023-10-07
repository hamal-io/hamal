package io.hamal.api.web.exec

import io.hamal.core.adapter.GetExecPort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetExecController(private val getExec: GetExecPort) {
    @GetMapping("/v1/execs/{execId}")
    fun get(@PathVariable("execId") execId: ExecId): ResponseEntity<ApiExec> {
        return getExec(execId) { exec ->
            ResponseEntity.ok(
                ApiExec(
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