package io.hamal.core.adapter.exec

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

fun interface ExecGetPort {
    operator fun invoke(execId: ExecId): Exec
}

@Component
class ExecGetAdapter(
    private val execQueryRepository: ExecQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : ExecGetPort {
    override fun invoke(execId: ExecId): Exec = ensureAccess(execQueryRepository.get(execId))
}