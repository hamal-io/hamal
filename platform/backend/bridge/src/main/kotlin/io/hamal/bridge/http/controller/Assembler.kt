package io.hamal.bridge.http.controller

import io.hamal.lib.sdk.bridge.BridgeExecCompleteSubmitted
import io.hamal.lib.sdk.bridge.BridgeExecFailSubmitted
import io.hamal.lib.sdk.bridge.BridgeSubmitted
import io.hamal.lib.domain.submitted.ExecCompleteSubmitted
import io.hamal.lib.domain.submitted.ExecFailSubmitted
import io.hamal.lib.domain.submitted.Submitted
import org.springframework.http.ResponseEntity

fun Submitted.accepted(): ResponseEntity<BridgeSubmitted> =
    ResponseEntity.accepted().body(toBridgeSubmitted())

fun Submitted.toBridgeSubmitted(): BridgeSubmitted = when (this) {
    is ExecCompleteSubmitted -> BridgeExecCompleteSubmitted(id, status, execId)
    is ExecFailSubmitted -> BridgeExecFailSubmitted(id, status, execId)
    else -> throw NotImplementedError()
}
