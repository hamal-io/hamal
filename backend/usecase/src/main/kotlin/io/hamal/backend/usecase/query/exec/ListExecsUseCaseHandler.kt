package io.hamal.backend.usecase.query.exec

import io.hamal.backend.core.exec.Exec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.query.ExecQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListExecsUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryManyUseCaseHandler<Exec, ExecQuery.ListExec>(ExecQuery.ListExec::class) {
    override fun invoke(useCase: ExecQuery.ListExec): List<Exec> {
        return execQueryRepository.list(useCase.limit)
    }
}