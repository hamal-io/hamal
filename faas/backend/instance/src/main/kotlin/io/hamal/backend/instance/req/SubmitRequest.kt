package io.hamal.backend.instance.req

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


data class InvokeFixedRate(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
)

data class InvokeEvent(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val invocation: EventInvocation
)

@Component
class SubmitRequest(
    @Autowired private val generateDomainId: GenerateDomainId,
    @Autowired private val reqCmdRepository: ReqCmdRepository,
) {
    operator fun invoke(adhoc: InvokeAdhocReq) =
        SubmittedInvokeAdhocReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::ExecId),
            inputs = adhoc.inputs,
            code = adhoc.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, oneshot: InvokeOneshotReq) =
        SubmittedInvokeOneshotReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = funcId,
            correlationId = oneshot.correlationId ?: CorrelationId("__default__"),
            inputs = oneshot.inputs,
        ).also(reqCmdRepository::queue)

    operator fun invoke(fixedRate: InvokeFixedRate) =
        SubmittedInvokeFixedRateReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
        ).also(reqCmdRepository::queue)


    operator fun invoke(invoke: InvokeEvent) =
        SubmittedInvokeEventReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = invoke.funcId,
            correlationId = invoke.correlationId,
            inputs = invoke.inputs,
            invocation = invoke.invocation
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, complete: CompleteExecReq) =
        SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            state = complete.state,
            events = complete.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, fail: FailExecReq) =
        SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            cause = fail.cause
        ).also(reqCmdRepository::queue)

    operator fun invoke(createFuncReq: CreateFuncReq) =
        SubmittedCreateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::FuncId),
            name = createFuncReq.name,
            inputs = createFuncReq.inputs,
            code = createFuncReq.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(createTriggerReq: CreateTriggerReq) =
        SubmittedCreateTriggerReq(
            type = createTriggerReq.type,
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::TriggerId),
            name = createTriggerReq.name,
            funcId = createTriggerReq.funcId,
            inputs = createTriggerReq.inputs,
            correlationId = createTriggerReq.correlationId,
            duration = createTriggerReq.duration,
            topicId = createTriggerReq.topicId
        ).also(reqCmdRepository::queue)

    operator fun invoke(createTopic: CreateTopicReq) =
        SubmittedCreateTopicReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = generateDomainId(::TopicId),
            name = createTopic.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(appendEvent: AppendEventReq) =
        SubmittedAppendEventReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = appendEvent.topicId,
            event = appendEvent.event
        ).also(reqCmdRepository::queue)

    operator fun invoke(setStateReq: SetStateReq) =
        SubmittedSetStateReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            state = CorrelatedState(
                correlation = setStateReq.correlation,
                value = setStateReq.value
            )
        ).also(reqCmdRepository::queue)
}

