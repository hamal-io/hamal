package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.ExecId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CompleteExecRoute(
    val request: SubmitRequest
) {
    @PostMapping("/v1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody complete: CompleteExecReq
    ): ResponseEntity<SubmittedCompleteExecReq> {
        val result = request(execId, complete)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}