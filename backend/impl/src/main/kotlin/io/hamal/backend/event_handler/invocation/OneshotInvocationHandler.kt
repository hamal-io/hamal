package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.OneshotInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.lib.domain.Correlation
import io.hamal.backend.repository.api.domain.OneshotInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId

class OneshotInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<OneshotInvocationEvent> {
    private val log = logger(OneshotInvocationHandler::class)
    override fun handle(evt: OneshotInvocationEvent) {
        log.debug("Handle: ${evt}")
        val func = evt.func

        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = func.shard,
                code = func.code,
                correlation = Correlation(
                    correlationId = evt.correlationId,
                    funcId = func.id
                ),
                // FIXME func for audit purpose ?
                invocation = OneshotInvocation()
            )
        )
    }
}