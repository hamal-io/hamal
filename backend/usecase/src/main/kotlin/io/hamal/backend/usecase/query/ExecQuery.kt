package io.hamal.backend.usecase.query

import io.hamal.backend.core.exec.StartedExec
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.ExecId

object ExecQuery {

    data class GetStartedExec(
        val execId: ExecId
    ) : QueryOneUseCase<StartedExec>

}