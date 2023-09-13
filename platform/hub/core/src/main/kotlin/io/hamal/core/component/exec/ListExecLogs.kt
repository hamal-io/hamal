package io.hamal.core.component.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogQueryRepository
import org.springframework.stereotype.Component

@Component
class ListExecLogs(private val execLogQueryRepository: ExecLogQueryRepository) {
    operator fun <T : Any> invoke(
        execId: ExecId,
        query: ExecLogQueryRepository.ExecLogQuery,
        responseHandler: (List<ExecLog>) -> T
    ): T {
        return responseHandler(execLogQueryRepository.list(execId, query))
    }
}