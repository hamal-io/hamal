package io.hamal.core.adapter.state

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.StateQueryRepository
import org.springframework.stereotype.Component

fun interface StateGetPort {
    operator fun invoke(funcId: FuncId, correlationId: CorrelationId): CorrelatedState
}

@Component
class StateGetAdapter(
    private val stateQueryRepository: StateQueryRepository,
    private val funcGet: FuncGetPort
) : StateGetPort {
    override fun invoke(funcId: FuncId, correlationId: CorrelationId): CorrelatedState {
        ensureFuncAccess(funcId)
        return stateQueryRepository.get(Correlation(correlationId, funcId))
    }

    private fun ensureFuncAccess(funcId: FuncId) {
        funcGet(funcId)
    }
}
