package io.hamal.backend.event_handler.invocation

import io.hamal.backend.event.FixedDelayInvocationEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.logger
import io.hamal.backend.repository.api.domain.FixedDelayInvocation
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets

class FixedDelayInvocationHandler(
    val execCmdService: ExecCmdService
) : EventHandler<FixedDelayInvocationEvent> {
    private val log = logger(FixedDelayInvocationHandler::class)
    override fun handle(computeId: ComputeId, evt: FixedDelayInvocationEvent) {
        log.debug("Handle: ${evt}")
        val func = evt.func


        execCmdService.plan(
            computeId, ExecCmdService.ToPlan(
                computeId = ComputeId(123),
                shard = func.shard,
                correlation = Correlation(
                    correlationId = evt.correlationId,
                    funcId = func.id
                ),
                inputs = evt.inputs.toExecInputs(),
                secrets = evt.secrets.toExecSecrets(),
                code = func.code,
                // FIXME func for audit purpose ?
                invocation = FixedDelayInvocation()
            )
        )
    }
}

private fun InvocationInputs.toExecInputs() = ExecInputs(this.value)

private fun InvocationSecrets.toExecSecrets() = ExecSecrets(this.value)