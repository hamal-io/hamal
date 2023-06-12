package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.SecureRandom

data class InvokeAdhoc(
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
)

data class InvokeOneshot(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets
)

data class InvokeFixedRate(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets
)

data class InvokeEvent(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets
)

data class CompleteExec(
    val execId: ExecId,
    val statePayload: StatePayload
)

@Component
class Request(
    @Autowired private val reqCmdRepository: ReqCmdRepository,
    @Autowired private val generateDomainId: GenerateDomainId
) {
    operator fun invoke(adhoc: InvokeAdhoc): InvokeAdhocReq {
        return InvokeAdhocReq(
            id = reqId(),
            status = ReqStatus.Received,
            execId = generateDomainId(Shard(1), ::ExecId),
            inputs = adhoc.inputs,
            secrets = adhoc.secrets,
            code = adhoc.code
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(oneshot: InvokeOneshot): InvokeOneshotReq {
        return InvokeOneshotReq(
            id = reqId(),
            status = ReqStatus.Received,
            execId = generateDomainId(Shard(1), ::ExecId),
            funcId = oneshot.funcId,
            correlationId = oneshot.correlationId,
            inputs = oneshot.inputs,
            secrets = oneshot.secrets,
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(fixedRate: InvokeFixedRate): InvokeFixedRateReq {
        return InvokeFixedRateReq(
            id = reqId(),
            status = ReqStatus.Received,
            execId = generateDomainId(Shard(1), ::ExecId),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
            secrets = fixedRate.secrets,
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(evt: InvokeEvent): InvokeEventReq {
        return InvokeEventReq(
            id = reqId(),
            status = ReqStatus.Received,
            execId = generateDomainId(Shard(1), ::ExecId),
            funcId = evt.funcId,
            correlationId = evt.correlationId,
            inputs = evt.inputs,
            secrets = evt.secrets,
        ).also(reqCmdRepository::queue)
    }


    operator fun invoke(complete: CompleteExec): CompleteExecReq {
        return CompleteExecReq(
            id = reqId(),
            status = ReqStatus.Received,
            execId = complete.execId,
            statePayload = complete.statePayload
        ).also(reqCmdRepository::queue)
    }


    private fun reqId() = ReqId(BigInteger(128, SecureRandom()))

}


