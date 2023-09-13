package io.hamal.core.component.state

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.SetStateReq
import org.springframework.stereotype.Component

@Component
class SetState(
    private val funcQueryRepository: FuncQueryRepository,
    private val submitRequest: SubmitRequest
) {
    operator fun <T : Any> invoke(
        req: SetStateReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureFuncExists(req.correlation.funcId)
        return responseHandler(submitRequest(req))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}