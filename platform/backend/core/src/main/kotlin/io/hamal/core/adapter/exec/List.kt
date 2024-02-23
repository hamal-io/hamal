package io.hamal.core.adapter.exec

import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

fun interface ExecListPort {
    operator fun invoke(query: ExecQueryRepository.ExecQuery): List<Exec>
}

@Component
class ExecListAdapter(
    private val execQueryRepository: ExecQueryRepository
) : ExecListPort {
    override fun invoke(query: ExecQueryRepository.ExecQuery): List<Exec> = execQueryRepository.list(query)
}