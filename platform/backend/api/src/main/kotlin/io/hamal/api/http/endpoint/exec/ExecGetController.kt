package io.hamal.api.http.endpoint.exec

import io.hamal.core.adapter.GetExecPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExec
import io.hamal.repository.api.CompletedExec
import io.hamal.repository.api.FailedExec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecGetController(
    private val retry: Retry,
    private val getExec: GetExecPort
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
                        code = exec.code.let { code ->
                            ApiExec.Code(
                                id = code.id,
                                version = code.version,
                                value = code.value
                            )
                        },
                        events = exec.events,
                        result = if (exec is CompletedExec) {
                            exec.result
                        } else if (exec is FailedExec) {
                            exec.result
                        } else {
                            null
                        }
                    )
                )
            }
        }
    }
}