package io.hamal.backend.usecase.query

import io.hamal.backend.core.func.Func
import io.hamal.lib.domain.ddd.QueryManyUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.FuncId

object FuncQuery {

    data class GetFunc(
        val funcId: FuncId
    ) : QueryOneUseCase<Func>


    data class ListFunc(
        val afterId: FuncId,
        val limit: Int
    ) : QueryManyUseCase<Func>

}