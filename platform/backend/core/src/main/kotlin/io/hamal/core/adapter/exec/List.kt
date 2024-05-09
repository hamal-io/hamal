package io.hamal.core.adapter.exec

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

fun interface ExecListPort {
    operator fun invoke(query: ExecQueryRepository.ExecQuery): List<Exec>
}

@Component
class ExecListAdapter(
    private val execQueryRepository: ExecQueryRepository,
    private val accessFilterAccess: FilterAccessPort
) : ExecListPort {
    override fun invoke(query: ExecQueryRepository.ExecQuery): List<Exec> = accessFilterAccess(execQueryRepository.list(query))
}