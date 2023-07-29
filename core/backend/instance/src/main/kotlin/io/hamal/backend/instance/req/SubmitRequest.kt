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
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            inputs = adhoc.inputs,
            code = adhoc.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, oneshot: InvokeOneshotReq) =
        SubmittedInvokeOneshotReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = funcId,
            correlationId = oneshot.correlationId ?: CorrelationId("__default__"),
            inputs = oneshot.inputs,
        ).also(reqCmdRepository::queue)

    operator fun invoke(fixedRate: InvokeFixedRate) =
        SubmittedInvokeFixedRateReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
        ).also(reqCmdRepository::queue)


    operator fun invoke(invoke: InvokeEvent) =
        SubmittedInvokeEventReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = invoke.funcId,
            correlationId = invoke.correlationId,
            inputs = invoke.inputs,
            invocation = invoke.invocation
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, complete: CompleteExecReq) =
        SubmittedCompleteExecReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = execId,
            state = complete.state,
            events = complete.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(createFuncReq: CreateFuncReq) =
        SubmittedCreateFuncReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            funcId = generateDomainId(::FuncId),
            funcName = createFuncReq.name,
            inputs = createFuncReq.inputs,
            code = createFuncReq.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(createTriggerReq: CreateTriggerReq) =
        SubmittedCreateTriggerReq(
            type = createTriggerReq.type,
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            triggerId = generateDomainId(::TriggerId),
            triggerName = createTriggerReq.name,
            funcId = createTriggerReq.funcId,
            inputs = createTriggerReq.inputs,
            correlationId = createTriggerReq.correlationId,
            duration = createTriggerReq.duration,
            topicId = createTriggerReq.topicId
        ).also(reqCmdRepository::queue)

    operator fun invoke(createTopic: CreateTopicReq) =
        SubmittedCreateTopicReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            topicId = generateDomainId(::TopicId),
            name = createTopic.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(appendEvent: AppendEventReq) =
        SubmittedAppendEventReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            topicId = appendEvent.topicId,
            event = appendEvent.event
        ).also(reqCmdRepository::queue)

    operator fun invoke(setStateReq: SetStateReq) =
        SubmittedSetStateReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            state = CorrelatedState(
                correlation = setStateReq.correlation,
                value = setStateReq.value
            )
        ).also(reqCmdRepository::queue)
}

