package io.hamal.backend.usecase

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.lib.domain.ddd.QueryManyUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.FuncId

object FuncQueryUseCase {

    data class GetFunc(
        val funcId: FuncId
    ) : QueryOneUseCase<Func>


    data class ListFunc(
        val afterId: FuncId,
        val limit: Int
    ) : QueryManyUseCase<Func>

}