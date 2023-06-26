package io.hamal.backend.instance.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.SecureRandom


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

@Serializable
data class CompleteExec(
    val execId: ExecId,
    val statePayload: State,
    val events: List<Event>
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


    operator fun invoke(funcId: FuncId, oneshot: InvokeOneshotReq) =
        SubmittedInvokeOneshotReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = funcId,
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

    operator fun invoke(execId: ExecId, complete: CompleteExecReq) =
        SubmittedCompleteExecReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            execId = execId,
            state = complete.state,
            events = complete.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(createFuncReq: CreateFuncReq) =
        SubmittedCreateFuncReq(
            id = reqId(),
            status = ReqStatus.Submitted,
            funcId = generateDomainId(::FuncId),
            funcName = createFuncReq.name,
            inputs = createFuncReq.inputs,
            secrets = createFuncReq.secrets,
            code = createFuncReq.code
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

