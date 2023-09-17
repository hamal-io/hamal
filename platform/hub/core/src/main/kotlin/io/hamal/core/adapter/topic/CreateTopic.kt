package io.hamal.core.adapter.topic

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopic(private val submitRequest: SubmitRequest) {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateTopicReq,
        responseHandler: (SubmittedReq) -> T
    ): T = responseHandler(submitRequest(req))
}