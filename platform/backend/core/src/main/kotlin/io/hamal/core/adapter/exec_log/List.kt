package io.hamal.core.adapter.exec_log

import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogQueryRepository
import org.springframework.stereotype.Component

fun interface ExecLogListPort {
    operator fun invoke(query: ExecLogQueryRepository.ExecLogQuery): List<ExecLog>
}

@Component
class ExecLogListAdapter(
    private val execLogQueryRepository: ExecLogQueryRepository
) : ExecLogListPort {
    override fun invoke(query: ExecLogQueryRepository.ExecLogQuery): List<ExecLog> = execLogQueryRepository.list(query)
}