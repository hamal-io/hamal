package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.InvokeAdhocReq
import org.springframework.stereotype.Component


interface InvokeAdhocPort {
    operator fun <T : Any> invoke(groupId: GroupId, req: InvokeAdhocReq, responseHandler: (SubmittedReq) -> T): T
}

interface AdhocPort : InvokeAdhocPort

@Component
class AdhocAdapter(private val submitRequest: SubmitRequest) : AdhocPort {
    override operator fun <T : Any> invoke(
        groupId: GroupId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReq) -> T
    ): T = responseHandler(submitRequest(groupId, req))
}
