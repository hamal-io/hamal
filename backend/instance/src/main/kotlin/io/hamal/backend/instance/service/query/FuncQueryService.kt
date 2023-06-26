package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.FuncId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FuncQueryService(
    @Autowired val funcQueryRepository: FuncQueryRepository
) {
    fun get(funcId: FuncId): Func {
        return funcQueryRepository.find(funcId)
            ?: throw NoSuchElementException("Func not found")
    }

    fun find(funcId: FuncId): Func? {
        return funcQueryRepository.find(funcId)
    }

    fun list(block: FuncQueryRepository.FuncQuery.() -> Unit): List<Func> {
        return funcQueryRepository.list(block)
    }
}