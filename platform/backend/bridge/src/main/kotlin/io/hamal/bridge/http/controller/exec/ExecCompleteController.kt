package io.hamal.bridge.http.controller.exec

import io.hamal.bridge.http.controller.accepted
import io.hamal.core.adapter.ExecCompletePort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequest
import io.hamal.lib.sdk.bridge.BridgeRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecCompleteController(
    val execComplete: ExecCompletePort
) {
    @PostMapping("/b1/execs/{execId}/complete")
    fun complete(
        @PathVariable("execId") execId: ExecId,
        @RequestBody req: BridgeExecCompleteRequest
    ): ResponseEntity<BridgeRequested> = execComplete(execId, req).accepted()
}