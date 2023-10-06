package io.hamal.core.adapter

import io.hamal.lib.domain.ReqId
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import org.springframework.stereotype.Component


interface GetReqPort {
    operator fun <T : Any> invoke(reqId: ReqId, responseHandler: (SubmittedReq) -> T): T
}

interface ListReqPort {
    operator fun <T : Any> invoke(
        query: ReqQueryRepository.ReqQuery,
        responseHandler: (List<SubmittedReq>) -> T
    ): T
}

interface ReqPort : GetReqPort, ListReqPort

@Component
class ReqAdapter(private val reqQueryRepository: ReqQueryRepository) : ReqPort {
    
    override fun <T : Any> invoke(reqId: ReqId, responseHandler: (SubmittedReq) -> T): T =
        responseHandler(reqQueryRepository.get(reqId))

    override operator fun <T : Any> invoke(
        query: ReqQueryRepository.ReqQuery,
        responseHandler: (List<SubmittedReq>) -> T
    ): T = responseHandler(reqQueryRepository.list(query))
}