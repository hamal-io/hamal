package io.hamal.backend.instance.req.handler.invocation

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.req.handler.toExecInputs
import io.hamal.backend.instance.req.handler.toExecSecrets
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.ExecCmdService.ToPlan
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.req.InvokeEventReq
import org.springframework.stereotype.Component

@Component
class InvokeEventHandler(
    private val execCmdService: ExecCmdService,
    private val funcQueryService: FuncQueryService
) : ReqHandler<InvokeEventReq>(InvokeEventReq::class) {
    override fun invoke(req: InvokeEventReq) {
        val func = funcQueryService.get(req.funcId)
        execCmdService.plan(
            req.cmdId(), ToPlan(
                execId = req.execId,
                correlation = Correlation(
                    correlationId = req.correlationId,
                    funcId = func.id
                ),
                inputs = req.inputs.toExecInputs(),
                secrets = req.secrets.toExecSecrets(),
                code = func.code
            )
        )
    }
}