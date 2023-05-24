package io.hamal.backend.infra.usecase.query.func

import io.hamal.backend.core.func.Func
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.infra.usecase.query.FuncQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListFuncUseCaseHandler(
    val funcQueryRepository: FuncQueryRepository
) : QueryManyUseCaseHandler<Func, io.hamal.backend.infra.usecase.query.FuncQuery.ListFunc>(io.hamal.backend.infra.usecase.query.FuncQuery.ListFunc::class) {
    override fun invoke(useCase: io.hamal.backend.infra.usecase.query.FuncQuery.ListFunc): List<Func> {
        return funcQueryRepository.list(useCase.afterId, useCase.limit)
    }
}