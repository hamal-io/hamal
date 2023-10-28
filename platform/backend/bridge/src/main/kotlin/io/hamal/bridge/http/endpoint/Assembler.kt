package io.hamal.bridge.http.endpoint

import io.hamal.lib.sdk.bridge.BridgeExecCompleteSubmitted
import io.hamal.lib.sdk.bridge.BridgeExecFailSubmitted
import io.hamal.lib.sdk.bridge.BridgeSubmitted
import io.hamal.repository.api.submitted_req.ExecCompleteSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import io.hamal.repository.api.submitted_req.Submitted
import org.springframework.http.ResponseEntity

fun Submitted.accepted(): ResponseEntity<BridgeSubmitted> =
    ResponseEntity.accepted().body(toBridgeSubmitted())

fun Submitted.toBridgeSubmitted(): BridgeSubmitted = when (this) {
    is ExecCompleteSubmitted -> BridgeExecCompleteSubmitted(reqId, status, id)
    is ExecFailSubmitted -> BridgeExecFailSubmitted(reqId, status, id)
    else -> throw NotImplementedError()
}
