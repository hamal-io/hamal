package io.hamal.core.component.namespace

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.UpdateNamespaceReq
import org.springframework.stereotype.Component

@Component
class UpdateNamespace(
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val submitRequest: SubmitRequest,
) {

    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: UpdateNamespaceReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureNamespaceExists(namespaceId)
        return responseHandler(submitRequest(namespaceId, req))
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}