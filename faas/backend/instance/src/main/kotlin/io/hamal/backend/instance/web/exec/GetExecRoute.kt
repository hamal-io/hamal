package io.hamal.backend.instance.web.exec

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.lib.domain.Exec
import io.hamal.lib.domain.vo.ExecId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GetExecRoute(
    private val execQueryRepository: ExecQueryRepository,
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") execId: ExecId
    ): ResponseEntity<Exec> {
        return ResponseEntity.ok(execQueryRepository.get(execId))
    }
}