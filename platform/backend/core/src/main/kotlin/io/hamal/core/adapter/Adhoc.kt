package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.InvokeAdhocReq
import org.springframework.stereotype.Component


interface AdhocInvokePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReq) -> T
    ): T
}

interface AdhocPort : AdhocInvokePort

@Component
class AdhocAdapter(private val submitRequest: SubmitRequest) : AdhocPort {
    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReq) -> T
    ): T = responseHandler(submitRequest(namespaceId, req))
}
