package io.hamal.bridge.http.controller

import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.sdk.bridge.BridgeExecCompleteRequested
import io.hamal.lib.sdk.bridge.BridgeExecFailRequested
import io.hamal.lib.sdk.bridge.BridgeRequested
import org.springframework.http.ResponseEntity

fun Requested.accepted(): ResponseEntity<BridgeRequested> =
    ResponseEntity.accepted().body(toBridgeSubmitted())

fun Requested.toBridgeSubmitted(): BridgeRequested = when (this) {
    is ExecCompleteRequested -> BridgeExecCompleteRequested(requestId, requestStatus, id)
    is ExecFailRequested -> BridgeExecFailRequested(requestId, requestStatus, id)
    else -> throw NotImplementedError()
}
