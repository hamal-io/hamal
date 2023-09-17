package io.hamal.core.adapter.exec

import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

@Component
class ListExec(private val execQueryRepository: ExecQueryRepository) {
    operator fun <T : Any> invoke(
        query: ExecQueryRepository.ExecQuery,
        responseHandler: (List<Exec>) -> T
    ): T = responseHandler(execQueryRepository.list(query))
}
