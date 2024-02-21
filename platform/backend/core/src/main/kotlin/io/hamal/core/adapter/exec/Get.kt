package io.hamal.core.adapter.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

fun interface ExecGetPort {
    operator fun invoke(execId: ExecId): Exec
}

@Component
class ExecGetAdapter(
    private val execQueryRepository: ExecQueryRepository
) : ExecGetPort {
    override fun invoke(execId: ExecId): Exec = execQueryRepository.get(execId)
}