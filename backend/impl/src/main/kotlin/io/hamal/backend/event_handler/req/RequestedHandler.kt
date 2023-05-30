package io.hamal.backend.event_handler.req

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event.RequestedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.ReqPayload
import io.hamal.backend.service.cmd.ReqCmdService
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId


class RequestedHandler(
    val reqCmdService: ReqCmdService,
    val eventEmitter: EventEmitter
) : EventHandler<RequestedEvent> {
    override fun handle(evt: RequestedEvent) {
        println("PROCESS $evt")

        try {
            when (val payload = evt.req.payload) {
                is ReqPayload.InvokeAdhoc -> handle(evt.req.id, payload)
                else -> TODO()
            }
            // reqCmdService.complete(..)
        } catch (t: Throwable) {
            // reqCmdService.fail(..)
        }
    }
}

internal fun RequestedHandler.handle(reqId: ReqId, adhocReq: ReqPayload.InvokeAdhoc) {
    eventEmitter.emit(
        AdhocInvocationEvent(
            reqId = reqId,
            shard = Shard(1),
            inputs = adhocReq.inputs,
            secrets = adhocReq.secrets,
            code = adhocReq.code
        )
    )
}