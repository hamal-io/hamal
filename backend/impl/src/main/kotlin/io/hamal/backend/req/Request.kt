package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class InvokeAdhoc(
    val execId: ExecId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
)


@Component
class Request
@Autowired constructor(
    val reqCmdRepository: ReqCmdRepository
) {

    operator fun invoke(invokeAdhoc: InvokeAdhoc): Req {
        TODO()
    }

    fun invokeEvent(
        execId: ExecId,
        correlationId: CorrelationId,
        inputs: InvocationInputs,
        secrets: InvocationSecrets,
        funcId: FuncId,
        trigger: EventTrigger
    ): Req {
        TODO()
    }

    fun invokeOneshot(
        execId: ExecId,
        correlationId: CorrelationId,
        inputs: InvocationInputs,
        secrets: InvocationSecrets,
        funcId: FuncId
    ): Req {
        TODO()
    }

    fun invokeFixedRate(
        execId: ExecId,
        correlationId: CorrelationId,
        inputs: InvocationInputs,
        secrets: InvocationSecrets,
        func: Func,
        trigger: FixedRateTrigger
    ): Req {
        TODO()
    }

    fun completeExec(
        execId: ExecId,
        statePayload: StatePayload
    ): Req {
        TODO()
    }

//    fun request(reqPayload: ReqPayload): Req {
//        return ApiListReqResponse.Req(
//            id = ReqId(BigInteger(128, SecureRandom())),
//            status = ReqStatus.Received,
//            payload = reqPayload
//        ).also(reqCmdRepository::queue)
//    }
//

}


