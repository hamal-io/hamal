package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.ExecGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExec
import io.hamal.repository.api.Exec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecGetController(
    private val retry: Retry,
    private val getExec: ExecGetPort
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(@PathVariable("execId") execId: ExecId): ResponseEntity<ApiExec> {
        return retry {
            getExec(execId) { exec ->
                ResponseEntity.ok(
                    ApiExec(
                        id = exec.id,
                        status = exec.status,
                        correlation = exec.correlation,
                        inputs = exec.inputs,
                        invocation = exec.invocation,
                        result = if (exec is Exec.Completed) {
                            exec.result
                        } else if (exec is Exec.Failed) {
                            exec.result
                        } else {
                            null
                        },
                        state = if (exec is Exec.Completed) {
                            exec.state
                        } else {
                            null
                        }
                    )
                )
            }
        }
    }
}