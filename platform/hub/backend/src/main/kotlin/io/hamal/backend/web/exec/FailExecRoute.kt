package io.hamal.backend.web.exec

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FailExecRoute(
    val request: SubmitRequest
) {
    @PostMapping("/v1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody fail: FailExecReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val result = request(execId, fail)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}