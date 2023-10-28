package io.hamal.core.adapter

import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import org.springframework.stereotype.Component

interface ExecGetPort {
    operator fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T): T
}

interface ExecListPort {
    operator fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T): T
}

interface ExecPort : ExecGetPort, ExecListPort

@Component
class ExecAdapter(
    private val execQueryRepository: ExecQueryRepository
) : ExecPort {

    override fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T) =
        responseHandler(execQueryRepository.get(execId))

    override fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T) =
        responseHandler(execQueryRepository.list(query))

}
