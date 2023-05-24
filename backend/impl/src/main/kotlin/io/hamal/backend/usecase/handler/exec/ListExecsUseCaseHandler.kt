package io.hamal.backend.usecase.handler.exec

import io.hamal.backend.repository.api.domain.exec.Exec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.ExecQueryUseCase
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListExecsUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryManyUseCaseHandler<Exec, ExecQueryUseCase.ListExec>(ExecQueryUseCase.ListExec::class) {
    override fun invoke(useCase: ExecQueryUseCase.ListExec): List<Exec> {
        return execQueryRepository.list(useCase.afterId, useCase.limit)
    }
}