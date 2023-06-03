package io.hamal.backend.req.handler.invocation

import io.hamal.backend.repository.api.domain.FixedRateInvocation
import io.hamal.backend.repository.api.domain.InvokeFixedRateReq
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.computeId
import io.hamal.backend.req.handler.toExecInputs
import io.hamal.backend.req.handler.toExecSecrets
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.ExecCmdService.ToPlan
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.domain.Correlation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InvokeFixedRateHandler(
    @Autowired private val execCmdService: ExecCmdService,
    @Autowired private val funcQueryService: FuncQueryService
) : ReqHandler<InvokeFixedRateReq>(InvokeFixedRateReq::class) {
    override fun invoke(req: InvokeFixedRateReq) {
        val func = funcQueryService.get(req.funcId)
        execCmdService.plan(
            req.computeId(), ToPlan(
                execId = req.execId,
                code = func.code,
                correlation = Correlation(
                    correlationId = req.correlationId,
                    funcId = func.id
                ),
                inputs = req.inputs.toExecInputs(),
                secrets = req.secrets.toExecSecrets(),
                // FIXME func for audit purpose ?
                invocation = FixedRateInvocation()
            )
        )
    }
}