package io.hamal.backend.req.handler.invocation

import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.backend.req.handler.toExecInputs
import io.hamal.backend.req.handler.toExecSecrets
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InvokeAdhocHandler(
    @Autowired private val execCmdService: ExecCmdService,
) : ReqHandler<SubmittedInvokeAdhocReq>(SubmittedInvokeAdhocReq::class) {
    override fun invoke(req: SubmittedInvokeAdhocReq) {
        execCmdService.plan(
            req.cmdId(), ToPlan(
                execId = req.execId,
                correlation = null,
                inputs = req.inputs.toExecInputs(),
                secrets = req.secrets.toExecSecrets(),
                code = req.code
            )
        )
    }
}