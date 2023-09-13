package io.hamal.core.component.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

@Component
class GetExec(private val execQueryRepository: ExecQueryRepository) {
    operator fun <T : Any> invoke(
        execId: ExecId,
        responseHandler: (Exec) -> T
    ): T {
        return responseHandler(execQueryRepository.get(execId))
    }
}