package io.hamal.core.component.exec

import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogQueryRepository
import org.springframework.stereotype.Component

@Component
class ListExecsLogs(private val execLogQueryRepository: ExecLogQueryRepository) {
    operator fun <T : Any> invoke(
        query: ExecLogQueryRepository.ExecLogQuery,
        responseHandler: (List<ExecLog>) -> T
    ): T {
        return responseHandler(execLogQueryRepository.list(query))
    }
}