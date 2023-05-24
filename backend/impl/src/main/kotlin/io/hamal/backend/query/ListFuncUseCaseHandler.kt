package io.hamal.backend.query

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.query.FuncQueryUseCase
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListFuncUseCaseHandler(
    val funcQueryRepository: FuncQueryRepository
) : QueryManyUseCaseHandler<Func, FuncQueryUseCase.ListFunc>(FuncQueryUseCase.ListFunc::class) {
    override fun invoke(useCase: FuncQueryUseCase.ListFunc): List<Func> {
        return funcQueryRepository.list(useCase.afterId, useCase.limit)
    }
}