package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.FixedDelayInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.lib.domain.Correlation
import io.hamal.backend.repository.api.domain.FixedDelayInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId

class FixedDelayInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<FixedDelayInvocationEvent> {
    private val log = logger(FixedDelayInvocationHandler::class)
    override fun handle(evt: FixedDelayInvocationEvent) {
        log.debug("Handle: ${evt}")
        val func = evt.func


        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = func.shard,
                correlation = Correlation(
                    correlationId = evt.correlationId,
                    funcId = func.id
                ),
                code = func.code,
                // FIXME func for audit purpose ?
                invocation = FixedDelayInvocation()
            )
        )
    }
}