package io.hamal.core.adapter.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

fun interface ExecFindPort {
    operator fun invoke(execId: ExecId): Exec?
}

@Component
class ExecFindAdapter(
    private val execQueryRepository: ExecQueryRepository
) : ExecFindPort {
    override fun invoke(execId: ExecId): Exec? = execQueryRepository.find(execId)
}