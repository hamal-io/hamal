package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.EventInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.EventInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets

class EventInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<EventInvocationEvent> {
    private val log = logger(EventInvocationHandler::class)
    override fun handle(reqId: ReqId, evt: EventInvocationEvent) {
        log.debug("Handle: ${evt}")
        val func = evt.func

        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = ReqId(123),
                shard = func.shard,
                correlation = null,
                inputs = evt.inputs.toExecInputs(),
                secrets = evt.secrets.toExecSecrets(),
                code = func.code,
                // FIXME func for audit purpose ?
                invocation = EventInvocation()
            )
        )
    }
}

private fun InvocationInputs.toExecInputs() = ExecInputs(this.value)

private fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)