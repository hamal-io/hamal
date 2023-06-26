package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.SubmittedReq
import org.springframework.stereotype.Service

@Service
class ReqQueryService(
    private val reqQueryRepository: ReqQueryRepository
) {
    fun get(reqId: ReqId): SubmittedReq {
        return reqQueryRepository.find(reqId)
            ?: throw NoSuchElementException("Req not found")
    }

    fun find(reqId: ReqId): SubmittedReq? {
        return reqQueryRepository.find(reqId)
    }

    fun list(block: ReqQueryRepository.Query.() -> Unit): List<SubmittedReq> {
        return reqQueryRepository.list(block)
    }
}