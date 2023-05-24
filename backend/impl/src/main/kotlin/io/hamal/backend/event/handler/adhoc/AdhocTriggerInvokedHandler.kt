package io.hamal.backend.event.handler.adhoc

import io.hamal.backend.event.AdhocTriggerInvokedEvent
import io.hamal.backend.event.handler.EventHandler
import io.hamal.backend.cmd.ExecCmd
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import logger

class AdhocTriggerInvokedHandler(
    val request: InvokeRequestOneUseCasePort
) : EventHandler<AdhocTriggerInvokedEvent> {

    private val log = logger(this::class)

    override fun handle(notification: AdhocTriggerInvokedEvent) {
        log.debug("Handle: $notification")
        request(
            ExecCmd.PlanExec(
                reqId = ReqId(123),
                shard = notification.shard,
                trigger = notification.adhocTrigger,
                code = notification.adhocTrigger.code
            )
        )
    }
}