package io.hamal.backend.instance.req.handler.func

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.FuncCmdService
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import org.springframework.stereotype.Component

@Component
class CreateFuncHandler(
    private val funcCmdService: FuncCmdService
) : ReqHandler<SubmittedCreateFuncReq>(SubmittedCreateFuncReq::class) {
    override fun invoke(req: SubmittedCreateFuncReq) {
        funcCmdService.create(
            req.cmdId(),
            FuncCmdService.ToCreate(
                id = req.funcId,
                name = req.funcName,
                inputs = req.inputs,
                secrets = req.secrets,
                code = req.code
            )
        )
    }
}