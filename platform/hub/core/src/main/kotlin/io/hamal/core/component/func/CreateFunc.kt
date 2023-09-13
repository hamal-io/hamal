package io.hamal.core.component.func

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateFuncReq
import org.springframework.stereotype.Component

@Component
class CreateFunc(
    private val submitRequest: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateFuncReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureNamespaceIdExists(req)
        return responseHandler(submitRequest(groupId, req))
    }

    private fun ensureNamespaceIdExists(req: CreateFuncReq) {
        req.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}