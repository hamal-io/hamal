package io.hamal.backend.web.exec

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CompleteExecRoute(
    val request: SubmitRequest
) {
    @PostMapping("/v1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody complete: CompleteExecReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val result = request(execId, complete)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}