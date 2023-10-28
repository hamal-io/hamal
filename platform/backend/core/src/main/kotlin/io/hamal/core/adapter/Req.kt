package io.hamal.core.adapter

import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.repository.api.submitted_req.Submitted
import org.springframework.stereotype.Component


interface ReqGetPort {
    operator fun <T : Any> invoke(reqId: ReqId, responseHandler: (Submitted) -> T): T
}

interface ReqListPort {
    operator fun <T : Any> invoke(
        query: ReqQueryRepository.ReqQuery,
        responseHandler: (List<Submitted>) -> T
    ): T
}

interface ReqPort : ReqGetPort, ReqListPort

@Component
class ReqAdapter(private val reqQueryRepository: ReqQueryRepository) : ReqPort {

    override fun <T : Any> invoke(reqId: ReqId, responseHandler: (Submitted) -> T): T =
        responseHandler(reqQueryRepository.get(reqId))

    override operator fun <T : Any> invoke(
        query: ReqQueryRepository.ReqQuery,
        responseHandler: (List<Submitted>) -> T
    ): T = responseHandler(reqQueryRepository.list(query))
}