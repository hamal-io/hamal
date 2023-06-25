package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.lib.domain.Exec
import io.hamal.lib.domain.vo.ExecId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExecQueryService(
    @Autowired val execQueryRepository: ExecQueryRepository
) {
    fun get(execId: ExecId): Exec {
        return execQueryRepository.find(execId)
            ?: throw NoSuchElementException("Exec not found")
    }

    fun find(execId: ExecId): Exec? {
        return execQueryRepository.find(execId)
    }

    fun list(block: ExecQueryRepository.ExecQuery.() -> Unit): List<Exec> {
        return execQueryRepository.list(block)
    }
}