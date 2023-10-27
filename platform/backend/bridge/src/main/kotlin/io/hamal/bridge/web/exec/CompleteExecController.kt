package io.hamal.bridge.web.exec

import io.hamal.bridge.req.SubmitBridgeRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCompleteExecReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CompleteExecController(
    val request: SubmitBridgeRequest
) {
    @PostMapping("/b1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody complete: ApiCompleteExecReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val result = request(execId, complete)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                groupId = GroupId(1),
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}