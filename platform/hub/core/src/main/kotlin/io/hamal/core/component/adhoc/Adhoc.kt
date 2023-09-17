package io.hamal.core.component.adhoc

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.InvokeAdhocReq
import org.springframework.stereotype.Component


@Component
class Adhoc(private val submitRequest: SubmitRequest) {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: InvokeAdhocReq,
        responseHandler: (SubmittedReq) -> T
    ): T = responseHandler(submitRequest(groupId, req))
}
