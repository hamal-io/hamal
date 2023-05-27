package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.ApiInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.ApiInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId

class ApiInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<ApiInvocationEvent> {
    private val log = logger(ApiInvocationHandler::class)
    override fun handle(evt: ApiInvocationEvent) {
        log.debug("Handle: ${evt}")

        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = evt.shard,
                code = evt.func.code,
                // FIXME func for audit purpose ?
                invocation = ApiInvocation()
            )
        )
    }
}