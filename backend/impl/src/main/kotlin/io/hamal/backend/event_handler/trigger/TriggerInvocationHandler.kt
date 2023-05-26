package io.hamal.backend.event_handler.trigger

import io.hamal.backend.event.TriggerInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.trigger.TriggerInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.InvocationId

class TriggerInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<TriggerInvocationEvent> {
    private val log = logger(TriggerInvocationHandler::class)
    override fun handle(evt: TriggerInvocationEvent) {
        log.debug("Handle: ${evt}")
        val func = evt.func

        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = func.shard,
                code = func.code,
                // FIXME func for audit purpose ?
                invocation = TriggerInvocation(
                    id = InvocationId(0),
                    trigger = evt.trigger
                )
            )
        )
    }
}