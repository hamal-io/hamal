package io.hamal.admin.web.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.admin.AdminExec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetExecRoute(
    private val execQueryRepository: ExecQueryRepository,
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") execId: ExecId
    ): ResponseEntity<AdminExec> {
        return ResponseEntity.ok(execQueryRepository.get(execId).let {
            AdminExec(
                id = it.id,
                status = it.status,
                correlation = it.correlation,
                inputs = it.inputs,
                code = it.code,
                events = it.events
            )
        })
    }
}