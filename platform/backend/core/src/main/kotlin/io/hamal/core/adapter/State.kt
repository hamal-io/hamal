package io.hamal.core.adapter

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.StateSetRequest
import io.hamal.lib.domain.request.StateSetRequested
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.repository.api.StateQueryRepository
import org.springframework.stereotype.Component

interface StateGetPort {
    operator fun invoke(funcId: FuncId, correlationId: CorrelationId): CorrelatedState
}

interface StateSetPort {
    operator fun invoke(req: StateSetRequest): StateSetRequested
}

interface StatePort : StateGetPort, StateSetPort

@Component
class StateAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateId,
    private val requestCmdRepository: RequestCmdRepository,
    private val stateQueryRepository: StateQueryRepository
) : StatePort {

    override operator fun invoke(funcId: FuncId, correlationId: CorrelationId): CorrelatedState {
        return stateQueryRepository.get(Correlation(correlationId, funcId))
    }

    override operator fun invoke(req: StateSetRequest): StateSetRequested {
        ensureFuncExists(req.correlation.funcId)
        val func = funcQueryRepository.get(req.correlation.funcId)
        return StateSetRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = func.workspaceId,
            state = CorrelatedState(
                correlation = req.correlation,
                value = req.value
            )
        ).also(requestCmdRepository::queue)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}