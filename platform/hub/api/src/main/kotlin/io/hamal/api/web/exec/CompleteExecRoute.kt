package io.hamal.api.web.exec

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.HubCompleteExecReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
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
        @RequestBody complete: HubCompleteExecReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        val result = request(execId, complete)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}