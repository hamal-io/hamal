package io.hamal.core.route.adhoc

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.adhoc.InvokeAdhocReq
import org.springframework.stereotype.Component


@Component
class AdhocRoute(
    private val submitRequest: SubmitRequest
) {
    fun <T : Any> adhoc(
        groupId: GroupId,
        adhocInvocation: InvokeAdhocReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        return responseHandler(
            submitRequest(groupId, adhocInvocation)
        )
    }
}
