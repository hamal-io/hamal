package io.hamal.backend.usecase.query.func

import io.hamal.backend.core.func.Func
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.usecase.query.FuncQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListFuncUseCaseHandler(
    val funcQueryRepository: FuncQueryRepository
) : QueryManyUseCaseHandler<Func, FuncQuery.ListFunc>(FuncQuery.ListFunc::class) {
    override fun invoke(useCase: FuncQuery.ListFunc): List<Func> {
        return funcQueryRepository.list(useCase.afterId, useCase.limit)
    }
}