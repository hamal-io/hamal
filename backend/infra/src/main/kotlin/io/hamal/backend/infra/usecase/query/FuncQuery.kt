package io.hamal.backend.infra.usecase.query

import io.hamal.backend.repository.api.domain.func.Func
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