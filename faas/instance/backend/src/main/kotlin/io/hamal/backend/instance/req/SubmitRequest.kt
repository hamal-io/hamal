package io.hamal.backend.instance.req

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.submitted_req.*
import io.hamal.lib.domain.*
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import org.springframework.stereotype.Component


data class InvokeExec(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val invocation: Invocation,
    val code: CodeValue
)

@Component
class SubmitRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val funcQueryRepository: FuncQueryRepository
) {
    operator fun invoke(adhoc: InvokeAdhocReq) =
        SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            inputs = adhoc.inputs,
            code = adhoc.code,
            funcId = null,
            correlationId = null,
            invocation = AdhocInvocation()
        ).also(reqCmdRepository::queue)


    operator fun invoke(funcId: FuncId, invokeFunc: InvokeFuncReq): SubmittedInvokeExecReq {
        val func = funcQueryRepository.get(funcId)
        return SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            funcId = funcId,
            correlationId = invokeFunc.correlationId,
            inputs = invokeFunc.inputs ?: InvocationInputs(),
            invocation = FuncInvocation(),
            code = func.code
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(invokeExec: InvokeExec) =
        SubmittedInvokeExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::ExecId),
            funcId = invokeExec.funcId,
            correlationId = invokeExec.correlationId,
            inputs = invokeExec.inputs,
            invocation = invokeExec.invocation,
            code = invokeExec.code
        ).also(reqCmdRepository::queue)


    operator fun invoke(execId: ExecId, complete: CompleteExecReq) =
        SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = execId,
            state = complete.state,
            events = complete.events
        ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, fail: FailExecReq) =
        SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = execId,
            cause = fail.cause
        ).also(reqCmdRepository::queue)

    operator fun invoke(createFuncReq: CreateFuncReq) =
        SubmittedCreateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = generateDomainId(::FuncId),
            name = createFuncReq.name,
            inputs = createFuncReq.inputs,
            code = createFuncReq.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(funcId: FuncId, updateFuncReq: UpdateFuncReq) =
        SubmittedUpdateFuncReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = funcId,
            name = updateFuncReq.name,
            inputs = updateFuncReq.inputs,
            code = updateFuncReq.code
        ).also(reqCmdRepository::queue)

    operator fun invoke(createTriggerReq: CreateTriggerReq) =
        SubmittedCreateTriggerReq(
            type = createTriggerReq.type,
            reqId = generateDomainId(::ReqId),
            status = Submitted,
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
            status = Submitted,
            id = generateDomainId(::TopicId),
            name = createTopic.name
        ).also(reqCmdRepository::queue)

    operator fun invoke(appendEvent: AppendEventReq) =
        SubmittedAppendToTopicReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            id = appendEvent.topicId,
            event = appendEvent.event
        ).also(reqCmdRepository::queue)

    operator fun invoke(setStateReq: SetStateReq) =
        SubmittedSetStateReq(
            reqId = generateDomainId(::ReqId),
            status = Submitted,
            state = CorrelatedState(
                correlation = setStateReq.correlation,
                value = setStateReq.value
            )
        ).also(reqCmdRepository::queue)
}

