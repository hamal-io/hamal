package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
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
    @Autowired private val generateDomainId: GenerateDomainId,
    @Autowired private val reqCmdRepository: ReqCmdRepository,
) {
    operator fun invoke(adhoc: InvokeAdhocReq) =
        SubmittedInvokeAdhocReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            inputs = adhoc.inputs,
            secrets = adhoc.secrets,
            code = adhoc.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(oneshot: InvokeOneshot) =
        InvokeOneshotReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = oneshot.funcId,
            correlationId = oneshot.correlationId,
            inputs = oneshot.inputs,
            secrets = oneshot.secrets,
        ).also(reqCmdRepository::queue)

    operator fun invoke(fixedRate: InvokeFixedRate) =
        InvokeFixedRateReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
            secrets = fixedRate.secrets,
        ).also(reqCmdRepository::queue)


    operator fun invoke(evt: InvokeEvent) =
        InvokeEventReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
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
            topicId = generateDomainId(::TopicId),
            name = createTopic.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(appendEvent: AppendEventReq) =
        SubmittedAppendEventReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            topicId = appendEvent.topicId,
            contentType = appendEvent.contentType,
            bytes = appendEvent.bytes
        ).also(reqCmdRepository::queue)


    private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")
    private fun reqId() = ReqId(BigInteger(128, rnd))

}

