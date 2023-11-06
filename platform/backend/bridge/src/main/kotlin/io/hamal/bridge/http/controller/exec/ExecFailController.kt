package io.hamal.bridge.http.controller.exec

import io.hamal.bridge.http.controller.accepted
import io.hamal.core.adapter.ExecFailPort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.bridge.BridgeExecFailReq
import io.hamal.lib.sdk.bridge.BridgeSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecFailController(
    val fail: ExecFailPort
) {
    @PostMapping("/b1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") execId: ExecId, @RequestBody req: BridgeExecFailReq
    ): ResponseEntity<BridgeSubmitted> = fail(execId, req, ExecFailSubmitted::accepted)
}