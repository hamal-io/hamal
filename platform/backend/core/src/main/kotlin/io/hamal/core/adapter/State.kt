package io.hamal.core.adapter

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.StateQueryRepository
import io.hamal.repository.api.submitted_req.StateSetSubmitted
import io.hamal.request.SetStateReq
import org.springframework.stereotype.Component

interface StateGetPort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        correlationId: CorrelationId,
        responseHandler: (CorrelatedState, Func) -> T
    ): T
}

interface StateSetPort {
    operator fun <T : Any> invoke(req: SetStateReq, responseHandler: (StateSetSubmitted) -> T): T
}

interface StatePort : StateGetPort, StateSetPort

@Component
class StateAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
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
        req: SetStateReq,
        responseHandler: (StateSetSubmitted) -> T
    ): T {
        ensureFuncExists(req.correlation.funcId)
        val func = funcQueryRepository.get(req.correlation.funcId)
        return StateSetSubmitted(
            id = generateDomainId(::ReqId),
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