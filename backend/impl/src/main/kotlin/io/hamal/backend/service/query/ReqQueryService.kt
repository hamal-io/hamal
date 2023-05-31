package io.hamal.backend.service.query

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ComputeId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReqQueryService(
    @Autowired val reqQueryRepository: ReqQueryRepository
) {

    fun get(computeId: ComputeId): Req {
        return reqQueryRepository.find(computeId)!! //FIXME require and proper error message
    }

    fun find(computeId: ComputeId): Req? {
        return reqQueryRepository.find(computeId)
    }

    fun list(afterId: ComputeId, limit: Int): List<Req> {
        return reqQueryRepository.list(afterId, limit)
    }

}