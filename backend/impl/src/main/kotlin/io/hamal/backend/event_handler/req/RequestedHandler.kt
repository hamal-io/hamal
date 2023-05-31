package io.hamal.backend.event_handler.req

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event.ExecCompletionRequestedEvent
import io.hamal.backend.event.RequestedEvent
import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.ReqPayload.CompleteExec
import io.hamal.backend.repository.api.domain.ReqPayload.InvokeAdhoc
import io.hamal.backend.service.cmd.ReqCmdService
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId


class RequestedHandler(
    val reqCmdService: ReqCmdService,
    val eventEmitter: EventEmitter
) : EventHandler<RequestedEvent> {
    override fun handle(reqId: ReqId,  evt: RequestedEvent) {
        reqCmdService.inflight(evt.req)

        try {
            when (val payload = evt.req.payload) {
                is InvokeAdhoc -> handle(evt.req.id, payload)
                is CompleteExec -> handle(evt.req.id, payload)
                else -> TODO()
            }
        } catch (t: Throwable) {
            // reqCmdService.fail(..)
        }
    }
}

internal fun RequestedHandler.handle(reqId: ReqId, toInvoke: InvokeAdhoc) {
    eventEmitter.emit(
        AdhocInvocationEvent(
//            reqId = reqId,
            shard = Shard(1),
            inputs = toInvoke.inputs,
            secrets = toInvoke.secrets,
            code = toInvoke.code
        )
    )
}

internal fun RequestedHandler.handle(reqId: ReqId, toComplete: CompleteExec) {
    eventEmitter.emit(
        ExecCompletionRequestedEvent(
//            reqId = reqId,
            shard = Shard(1),
            execId = toComplete.execId,
            statePayload = toComplete.statePayload
        )
    )
}