package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.req.SubmittedFailExecReq
import io.hamal.lib.domain.vo.ExecId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class FailExecRoute(
    val request: SubmitRequest
) {
    @PostMapping("/v1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody fail: FailExecReq
    ): ResponseEntity<SubmittedFailExecReq> {
        val result = request(execId, fail)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}