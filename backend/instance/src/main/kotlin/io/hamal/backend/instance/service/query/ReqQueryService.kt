package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.Req
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReqQueryService(
    @Autowired val reqQueryRepository: ReqQueryRepository
) {
    fun get(reqId: ReqId): Req {
        return reqQueryRepository.find(reqId)!! //FIXME require and proper error message
    }

    fun find(reqId: ReqId): Req? {
        return reqQueryRepository.find(reqId)
    }

    fun list(afterId: ReqId, limit: Int): List<Req> {
        return reqQueryRepository.query {
            this.afterId = afterId
            this.limit = limit
        }
    }

}