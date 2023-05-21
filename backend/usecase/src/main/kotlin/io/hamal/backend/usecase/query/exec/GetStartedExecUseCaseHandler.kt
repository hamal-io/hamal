package io.hamal.backend.usecase.query.exec

import io.hamal.backend.core.exec.StartedExec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.query.ExecQuery.GetStartedExec
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetStartedExecUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryOneUseCaseHandler<StartedExec, GetStartedExec>(GetStartedExec::class) {
    override fun invoke(useCase: GetStartedExec): StartedExec {
        return execQueryRepository.find(useCase.execId)?.let { require(it is StartedExec); return it }
            ?: TODO() // FIXME
    }
}