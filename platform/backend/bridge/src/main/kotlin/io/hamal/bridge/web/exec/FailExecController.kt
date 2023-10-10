package io.hamal.bridge.web.exec

import io.hamal.bridge.req.SubmitBridgeRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiFailExecReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FailExecController(
    val request: SubmitBridgeRequest
) {
    @PostMapping("/v1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody fail: ApiFailExecReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val result = request(execId, fail)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                groupId = it.groupId,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}