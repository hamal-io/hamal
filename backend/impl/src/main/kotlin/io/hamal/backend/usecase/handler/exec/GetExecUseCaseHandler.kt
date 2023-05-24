package io.hamal.backend.usecase.handler.exec

import io.hamal.backend.repository.api.domain.exec.Exec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.ExecQueryUseCase.GetExecUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetExecUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryOneUseCaseHandler<Exec, GetExecUseCase>(GetExecUseCase::class) {
    override fun invoke(useCase: GetExecUseCase): Exec {
        return execQueryRepository.find(useCase.execId) ?: TODO() // FIXME
    }
}