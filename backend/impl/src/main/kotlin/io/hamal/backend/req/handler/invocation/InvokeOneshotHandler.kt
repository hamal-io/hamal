package io.hamal.backend.req.handler.invocation

import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.backend.req.handler.toExecSecrets
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.req.InvokeOneshotReq
import io.hamal.lib.domain.vo.ExecInputs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InvokeOneshotHandler(
    @Autowired private val execCmdService: ExecCmdService,
    @Autowired private val funcQueryService: FuncQueryService
) : ReqHandler<InvokeOneshotReq>(InvokeOneshotReq::class) {
    override fun invoke(req: InvokeOneshotReq) {
        val func = funcQueryService.get(req.funcId)
        execCmdService.plan(
            req.cmdId(), ToPlan(
                execId = req.execId,
                tenantId = req.tenantId,
                code = func.code,
                correlation = Correlation(
                    correlationId = req.correlationId,
                    funcId = func.id
                ),
                inputs = ExecInputs(func.inputs.value),
                secrets = req.secrets.toExecSecrets()
            )
        )
    }
}