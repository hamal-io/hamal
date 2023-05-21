package io.hamal.backend.usecase.query

import io.hamal.backend.core.exec.Exec
import io.hamal.backend.core.exec.StartedExec
import io.hamal.lib.domain.ddd.QueryManyUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.ExecId

object ExecQuery {

    data class GetExec(
        val execId: ExecId
    ) : QueryOneUseCase<Exec>


    data class GetStartedExec(
        val execId: ExecId
    ) : QueryOneUseCase<StartedExec>

    data class ListExec(
        val limit: Int
    ) : QueryManyUseCase<Exec>

}