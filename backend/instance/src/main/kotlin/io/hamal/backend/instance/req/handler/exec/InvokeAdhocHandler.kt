package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.req.handler.toExecInputs
import io.hamal.backend.instance.req.handler.toExecSecrets
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.ExecCmdService.ToPlan
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import org.springframework.stereotype.Component

@Component
class InvokeAdhocHandler(
    private val execCmdService: ExecCmdService,
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