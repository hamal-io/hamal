package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.SecureRandom

data class InvokeAdhoc(
    val execId: ExecId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
)

data class CompleteExec(
    val execId: ExecId,
    val statePayload: StatePayload
)

@Component
class Request
@Autowired constructor(
    val reqCmdRepository: ReqCmdRepository
) {

    operator fun invoke(invokeAdhoc: InvokeAdhoc): Req {
        return InvokeAdhocReq(
            id = ReqId(BigInteger(128, SecureRandom())),
            status = ReqStatus.Received,
            execId = invokeAdhoc.execId,
            shard = Shard(1),
            inputs = invokeAdhoc.inputs,
            secrets = invokeAdhoc.secrets,
            code = invokeAdhoc.code
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(completeExec: CompleteExec): Req {
        return CompleteExecReq(
            id = ReqId(BigInteger(128, SecureRandom())),
            status = ReqStatus.Received,
            execId = completeExec.execId,
            shard = Shard(1),
            statePayload = completeExec.statePayload
        ).also(reqCmdRepository::queue)
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

//    fun completeExec(
//        execId: ExecId,
//        statePayload: StatePayload
//    ): Req {
//        TODO()
//    }

//    fun request(reqPayload: ReqPayload): Req {
//        return ApiListReqResponse.Req(
//            id = ReqId(BigInteger(128, SecureRandom())),
//            status = ReqStatus.Received,
//            payload = reqPayload
//        ).also(reqCmdRepository::queue)
//    }
//

}


