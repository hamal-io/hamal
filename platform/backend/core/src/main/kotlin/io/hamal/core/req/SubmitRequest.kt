package io.hamal.core.req

import io.hamal.core.adapter.GroupListPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecCompleteSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import org.springframework.stereotype.Component


data class InvokeExecReq(
    val execId: ExecId,
    val funcId: FuncId,
    val correlationId: CorrelationId?,
    val inputs: InvocationInputs,
    val code: ExecCode,
    val events: List<Event>
)

@Component
class SubmitRequest(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort
) {

    operator fun invoke(req: InvokeExecReq): ExecInvokeSubmitted {
        val func = funcQueryRepository.get(req.funcId)
        return ExecInvokeSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            groupId = func.groupId,
            funcId = req.funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = func.code.toExecCode(),
            events = listOf()
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: CompleteExecReq) = ExecCompleteSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = ExecFailSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

}
