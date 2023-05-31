package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.AdhocInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets

class AdhocInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<AdhocInvocationEvent> {
    private val log = logger(AdhocInvocationHandler::class)
    override fun handle(computeId: ComputeId, evt: AdhocInvocationEvent) {
        log.debug("Handle: ${evt}")

        execCmdService.plan(
            computeId,
            ExecCmdService.ToPlan(
                computeId = computeId, // FIXME move that out
                shard = evt.shard,
                correlation = null,
                inputs = evt.inputs.toExecInputs(),
                secrets = evt.secrets.toExecSecrets(),
                code = evt.code,
                // FIXME func for audit purpose ?
                invocation = AdhocInvocation()
            )
        )
    }
}

private fun InvocationInputs.toExecInputs() = ExecInputs(this.value)

private fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)