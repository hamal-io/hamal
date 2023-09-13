package io.hamal.core.component.func

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.UpdateFuncReq
import org.springframework.stereotype.Component

@Component
class UpdateFunc(
    private val funcQueryRepository: FuncQueryRepository,
    private val submitRequest: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        req: UpdateFuncReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureFuncExists(funcId)
        ensureNamespaceIdExists(req)
        return responseHandler(submitRequest(funcId, req))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }

    private fun ensureNamespaceIdExists(updateFunc: UpdateFuncReq) {
        updateFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}