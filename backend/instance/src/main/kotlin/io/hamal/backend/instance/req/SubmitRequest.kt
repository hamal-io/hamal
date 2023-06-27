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
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            inputs = adhoc.inputs,
            secrets = adhoc.secrets,
            code = adhoc.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, oneshot: InvokeOneshotReq) =
        SubmittedInvokeOneshotReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = funcId,
            correlationId = oneshot.correlationId,
            inputs = oneshot.inputs,
            secrets = oneshot.secrets,
        ).also(reqCmdRepository::queue)

    operator fun invoke(fixedRate: InvokeFixedRate) =
        InvokeFixedRateReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = fixedRate.funcId,
            correlationId = fixedRate.correlationId,
            inputs = fixedRate.inputs,
            secrets = fixedRate.secrets,
        ).also(reqCmdRepository::queue)


    operator fun invoke(evt: InvokeEvent) =
        InvokeEventReq(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = generateDomainId(::ExecId),
            funcId = evt.funcId,
            correlationId = evt.correlationId,
            inputs = evt.inputs,
            secrets = evt.secrets,
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
            secrets = createFuncReq.secrets,
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
            secrets = createTriggerReq.secrets,
            duration = createTriggerReq.duration,
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
            funcId = setStateReq.funcId,
            correlationId = setStateReq.correlationId,
            state = setStateReq.state
        ).also(reqCmdRepository::queue)
}

