package io.hamal.backend.usecase.query.func

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.usecase.query.FuncQuery.GetFunc
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetFuncUseCaseHandler(
    val funcQueryRepository: FuncQueryRepository
) : QueryOneUseCaseHandler<Func, GetFunc>(GetFunc::class) {
    override fun invoke(useCase: GetFunc): Func {
        return funcQueryRepository.find(useCase.funcId) ?: TODO() // FIXME
    }
}