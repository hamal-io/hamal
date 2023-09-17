package io.hamal.core.adapter.state

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.StateQueryRepository
import org.springframework.stereotype.Component

@Component
class GetState(
    private val funcQueryRepository: FuncQueryRepository,
    private val stateQueryRepository: StateQueryRepository
) {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        correlationId: CorrelationId,
        responseHandler: (CorrelatedState, Func) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)
        return responseHandler(
            stateQueryRepository.get(Correlation(correlationId, funcId)),
            func
        )
    }
}