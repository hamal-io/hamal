package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.StateQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.SetStateReq
import org.springframework.stereotype.Component

interface GetStatePort {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        correlationId: CorrelationId,
        responseHandler: (CorrelatedState, Func) -> T
    ): T
}

interface SetStatePort {
    operator fun <T : Any> invoke(req: SetStateReq, responseHandler: (SubmittedReqWithGroupId) -> T): T
}

interface StatePort : GetStatePort, SetStatePort


@Component
class StateAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val submitRequest: SubmitRequest,
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
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        ensureFuncExists(req.correlation.funcId)
        return responseHandler(submitRequest(req))
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}