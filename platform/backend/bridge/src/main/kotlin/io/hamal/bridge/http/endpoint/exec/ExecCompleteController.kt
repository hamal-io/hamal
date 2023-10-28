package io.hamal.bridge.http.endpoint.exec

import io.hamal.bridge.req.SubmitBridgeRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.bridge.BridgeExecCompleteReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecCompleteController(
    val request: SubmitBridgeRequest
) {
    @PostMapping("/b1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") execId: ExecId,
        @RequestBody complete: BridgeExecCompleteReq
    ): ResponseEntity<ApiSubmittedReqImpl<ExecId>> =
        request(execId, complete).let {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = null,
                        groupId = null,
                        id = it.id
                    )
                )
        }
}