package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.SubmittedReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReqQueryService(
    @Autowired val reqQueryRepository: ReqQueryRepository
) {
    fun get(reqId: ReqId): SubmittedReq {
        return reqQueryRepository.find(reqId)!! //FIXME require and proper error message
    }

    fun find(reqId: ReqId): SubmittedReq? {
        return reqQueryRepository.find(reqId)
    }

    fun list(afterId: ReqId, limit: Int): List<SubmittedReq> {
        return reqQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
    }

}