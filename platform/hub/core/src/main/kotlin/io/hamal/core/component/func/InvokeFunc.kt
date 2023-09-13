package io.hamal.core.component.func

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.InvokeFuncReq
import org.springframework.stereotype.Component

@Component
class InvokeFunc(
    private val submitRequest: SubmitRequest,
) {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: InvokeFuncReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        return responseHandler(submitRequest(funcId, req))
    }
}