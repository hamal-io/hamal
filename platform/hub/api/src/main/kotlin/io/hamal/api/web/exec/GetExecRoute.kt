package io.hamal.api.web.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.HubExec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetExecRoute(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository,
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") execId: ExecId
    ): ResponseEntity<HubExec> {
        return ResponseEntity.ok(execQueryRepository.get(execId).let {
            HubExec(
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