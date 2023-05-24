package io.hamal.backend.query

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.exec.Exec
import io.hamal.lib.domain.vo.ExecId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExecQueryService(
    @Autowired val execQueryRepository: ExecQueryRepository
) {
    fun get(execId: ExecId): Exec {
        return execQueryRepository.find(execId)!! //FIXME require and proper error message
    }

    fun find(execId: ExecId): Exec? {
        return execQueryRepository.find(execId)
    }

    fun list(afterId: ExecId, limit: Int): List<Exec> {
        return execQueryRepository.list(afterId, limit)
    }

}