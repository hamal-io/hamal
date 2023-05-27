package io.hamal.backend.service.query

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.domain.vo.FuncId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuncQueryService(
    @Autowired val funcQueryRepository: FuncQueryRepository
) {
    fun get(funcId: FuncId): Func {
        return funcQueryRepository.find(funcId)!! //FIXME require and proper error message
    }

    fun find(funcId: FuncId): Func? {
        return funcQueryRepository.find(funcId)
    }

    fun list(afterId: FuncId, limit: Int): List<Func> {
        return funcQueryRepository.list(afterId, limit)
    }

}