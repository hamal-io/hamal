package io.hamal.core.adapter.req

import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.springframework.stereotype.Component

@Component
class ListReq(private val reqQueryRepository: ReqQueryRepository) {
    operator fun <T : Any> invoke(
        query: ReqQueryRepository.ReqQuery,
        responseHandler: (List<SubmittedReq>) -> T
    ): T = responseHandler(reqQueryRepository.list(query))
}