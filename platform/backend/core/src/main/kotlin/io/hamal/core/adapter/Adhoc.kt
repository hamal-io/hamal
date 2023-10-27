package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.InvokeAdhocReq
import org.springframework.stereotype.Component


interface InvokeAdhocPort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface AdhocPort : InvokeAdhocPort

@Component
class AdhocAdapter(private val submitRequest: SubmitRequest) : AdhocPort {
    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T = responseHandler(submitRequest(namespaceId, req))
}
