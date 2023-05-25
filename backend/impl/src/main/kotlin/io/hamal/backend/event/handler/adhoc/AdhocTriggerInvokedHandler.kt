package io.hamal.backend.event.handler.adhoc

import io.hamal.backend.cmd.ExecCmdService
import io.hamal.backend.event.AdhocTriggerInvokedEvent
import io.hamal.backend.event.handler.EventHandler
import io.hamal.lib.domain.ReqId
import logger

class AdhocTriggerInvokedHandler(
    val cmdService: ExecCmdService
) : EventHandler<AdhocTriggerInvokedEvent> {

    private val log = logger(this::class)

    override fun handle(notification: AdhocTriggerInvokedEvent) {
        log.debug("Handle: $notification")
        cmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = notification.shard,
                trigger = notification.adhocTrigger,
                code = notification.adhocTrigger.code
            )
        )
    }
}