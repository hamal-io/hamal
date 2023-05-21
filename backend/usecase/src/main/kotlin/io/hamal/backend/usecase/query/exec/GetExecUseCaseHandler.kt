package io.hamal.backend.usecase.query.exec

import io.hamal.backend.core.exec.Exec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.query.ExecQuery.GetExec
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetExecUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryOneUseCaseHandler<Exec, GetExec>(GetExec::class) {
    override fun invoke(useCase: GetExec): Exec {
        return execQueryRepository.find(useCase.execId) ?: TODO() // FIXME
    }
}