package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.trigger.AdhocInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId

class AdhocInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<AdhocInvocationEvent> {
    private val log = logger(AdhocInvocationHandler::class)
    override fun handle(evt: AdhocInvocationEvent) {
        log.debug("Handle: ${evt}")

        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = evt.shard,
                code = evt.code,
                // FIXME func for audit purpose ?
                invocation = AdhocInvocation()
            )
        )
    }
}