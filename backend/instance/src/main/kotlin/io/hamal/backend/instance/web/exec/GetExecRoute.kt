package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.lib.domain.Exec
import io.hamal.lib.domain.vo.ExecId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class GetExecRoute(
    private val queryService: ExecQueryService,
) {
    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") execId: ExecId
    ): ResponseEntity<Exec> {
        return ResponseEntity.ok(queryService.get(execId))
    }
}