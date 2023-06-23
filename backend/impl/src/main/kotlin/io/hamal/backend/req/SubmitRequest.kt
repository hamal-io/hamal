package io.hamal.backend.req

import io.hamal.backend.WebContext
import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.SecureRandom


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
class SubmitRequest(
    @Autowired private val reqCmdRepository: ReqCmdRepository,
    @Autowired private val context: WebContext
) {
    operator fun invoke(adhoc: InvokeAdhocReq) =
        SubmittedInvokeAdhocReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = context.generateDomainId(::ExecId),
            tenantId = context.tenantId(),
            inputs = adhoc.inputs,
            secrets = adhoc.secrets,
            code = adhoc.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(oneshot: InvokeOneshot) =
        InvokeOneshotReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = context.generateDomainId(::ExecId),
            tenantId = context.tenantId(),
            funcId = oneshot.funcId,
            correlationId = oneshot.correlationId,
            inputs = oneshot.inputs,
            secrets = oneshot.secrets,
        ).also(reqCmdRepository::queue)

    operator fun invoke(fixedRate: InvokeFixedRate) =
        InvokeFixedRateReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = context.generateDomainId(::ExecId),
            tenantId = context.tenantId(),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
            secrets = fixedRate.secrets,
        ).also(reqCmdRepository::queue)


    operator fun invoke(evt: InvokeEvent) =
        InvokeEventReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = context.generateDomainId(::ExecId),
            tenantId = context.tenantId(),
            funcId = evt.funcId,
            correlationId = evt.correlationId,
            inputs = evt.inputs,
            secrets = evt.secrets,
        ).also(reqCmdRepository::queue)

    operator fun invoke(complete: CompleteExec) =
        CompleteExecReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = complete.execId,
            statePayload = complete.statePayload
        ).also(reqCmdRepository::queue)


    operator fun invoke(createTopic: CreateTopicReq) =
        SubmittedCreateTopicReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            topicId = context.generateDomainId(::TopicId),
            tenantId = context.tenantId(),
            name = createTopic.name
        ).also(reqCmdRepository::queue)


    private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")
    private fun reqId() = ReqId(BigInteger(128, rnd))

}

