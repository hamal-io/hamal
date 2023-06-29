package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.ExecCmdService.ToPlan
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.req.InvokeFixedRateReq
import org.springframework.stereotype.Component

@Component
class InvokeFixedRateHandler(
    private val execCmdService: ExecCmdService,
    private val funcQueryService: FuncQueryService
) : ReqHandler<InvokeFixedRateReq>(InvokeFixedRateReq::class) {
    override fun invoke(req: InvokeFixedRateReq) {
        val func = funcQueryService.get(req.funcId)
        execCmdService.plan(
            req.cmdId(), ToPlan(
                execId = req.execId,
                correlation = Correlation(
                    correlationId = req.correlationId,
                    funcId = func.id
                ),
                inputs = merge(func.inputs, req.inputs),
                secrets = merge(func.secrets, req.secrets),
                code = func.code,
            )
        )
    }
}