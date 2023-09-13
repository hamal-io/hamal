package io.hamal.core.component.namespace

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateNamespaceReq
import org.springframework.stereotype.Component

@Component
class CreateNamespace(private val submitRequest: SubmitRequest) {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateNamespaceReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        return responseHandler(submitRequest(groupId, req))
    }
}