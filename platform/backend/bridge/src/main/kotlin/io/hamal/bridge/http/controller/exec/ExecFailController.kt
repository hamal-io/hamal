package io.hamal.bridge.http.controller.exec

import io.hamal.bridge.http.controller.accepted
import io.hamal.core.adapter.exec.ExecFailPort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.bridge.BridgeExecFailRequest
import io.hamal.lib.sdk.bridge.BridgeRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecFailController(
    val execFail: ExecFailPort
) {
    @PostMapping("/b1/execs/{execId}/fail")
    fun fail(
        @PathVariable("execId") execId: ExecId, @RequestBody req: BridgeExecFailRequest
    ): ResponseEntity<BridgeRequested> = execFail(execId, req).accepted()
}