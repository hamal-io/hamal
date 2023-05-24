package io.hamal.backend.usecase.query.exec

import io.hamal.backend.repository.api.domain.exec.Exec
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.usecase.query.ExecQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListExecsUseCaseHandler(
    val execQueryRepository: ExecQueryRepository
) : QueryManyUseCaseHandler<Exec, io.hamal.backend.usecase.query.ExecQuery.ListExec>(io.hamal.backend.usecase.query.ExecQuery.ListExec::class) {
    override fun invoke(useCase: io.hamal.backend.usecase.query.ExecQuery.ListExec): List<Exec> {
        return execQueryRepository.list(useCase.afterId, useCase.limit)
    }
}