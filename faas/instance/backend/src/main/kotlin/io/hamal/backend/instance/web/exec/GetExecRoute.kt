package io.hamal.backend.instance.web.exec

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.domain.ApiExec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetExecRoute(
    private val execQueryRepository: ExecQueryRepository,
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") execId: ExecId
    ): ResponseEntity<ApiExec> {
        return ResponseEntity.ok(execQueryRepository.get(execId).let {
            ApiExec(
                id = it.id,
                status = it.status,
                correlation = it.correlation,
                inputs = it.inputs,
                code = it.code,
                invocation = it.invocation
            )
        })
    }
}