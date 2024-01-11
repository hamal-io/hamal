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
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.repository.api.StateQueryRepository
import org.springframework.stereotype.Component

interface StateGetPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        correlationId: CorrelationId,
        responseHandler: (CorrelatedState, Func) -> T
    ): T
}

interface StateSetPort {
    operator fun <T : Any> invoke(req: StateSetRequest, responseHandler: (StateSetRequested) -> T): T
}

interface StatePort : StateGetPort, StateSetPort

@Component
class StateAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: RequestCmdRepository,
    private val stateQueryRepository: StateQueryRepository
) : StatePort {

    override operator fun <T : Any> invoke(
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

    override operator fun <T : Any> invoke(
        req: StateSetRequest,
        responseHandler: (StateSetRequested) -> T
    ): T {
        ensureFuncExists(req.correlation.funcId)
        val func = funcQueryRepository.get(req.correlation.funcId)
        return StateSetRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            groupId = func.groupId,
            state = CorrelatedState(
                correlation = req.correlation,
                value = req.value
            )
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}