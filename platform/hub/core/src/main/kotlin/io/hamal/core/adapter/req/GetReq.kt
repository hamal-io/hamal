package io.hamal.core.adapter.req

import io.hamal.lib.domain.ReqId
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.springframework.stereotype.Component

@Component
class GetReq(private val reqQueryRepository: ReqQueryRepository) {
    operator fun <T : Any> invoke(
        reqId: ReqId,
        responseHandler: (SubmittedReq) -> T
    ): T = responseHandler(reqQueryRepository.get(reqId))
}