package io.hamal.backend.req.handler.invocation

import io.hamal.backend.repository.api.domain.AdhocInvocation
import io.hamal.backend.repository.api.domain.InvokeAdhocReq
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.commandId
import io.hamal.backend.req.handler.toExecInputs
import io.hamal.backend.req.handler.toExecSecrets
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InvokeAdhocHandler(
    @Autowired private val execCmdService: ExecCmdService,
) : ReqHandler<InvokeAdhocReq>(InvokeAdhocReq::class) {
    override fun invoke(req: InvokeAdhocReq) {
        execCmdService.plan(
            req.commandId(), ToPlan(
                execId = req.execId,
                correlation = null,
                inputs = req.inputs.toExecInputs(),
                secrets = req.secrets.toExecSecrets(),
                code = req.code,
                // FIXME func for audit purpose ?
                invocation = AdhocInvocation()
            )
        )
    }
}