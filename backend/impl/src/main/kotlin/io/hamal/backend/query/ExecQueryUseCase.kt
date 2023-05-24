package io.hamal.backend.query

import io.hamal.backend.repository.api.domain.exec.Exec
import io.hamal.backend.repository.api.domain.exec.StartedExec
import io.hamal.lib.domain.ddd.QueryManyUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.ExecId

object ExecQueryUseCase {

    data class GetExecUseCase(
        val execId: ExecId
    ) : QueryOneUseCase<Exec>


    data class GetStartedExec(
        val execId: ExecId
    ) : QueryOneUseCase<StartedExec>

    data class ListExec(
        val afterId: ExecId,
        val limit: Int
    ) : QueryManyUseCase<Exec>

}