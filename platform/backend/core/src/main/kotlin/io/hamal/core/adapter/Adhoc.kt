package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import io.hamal.request.AdhocInvokeReq
import org.springframework.stereotype.Component


interface AdhocInvokePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: AdhocInvokeReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T
}

interface AdhocPort : AdhocInvokePort

@Component
class AdhocAdapter(
    private val generateDomainId: GenerateDomainId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : AdhocPort {
    override operator fun <T : Any> invoke(
        flowId: FlowId,
        req: AdhocInvokeReq,
        responseHandler: (ExecInvokeSubmitted) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return ExecInvokeSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            flowId = flow.id,
            groupId = flow.groupId,
            inputs = req.inputs,
            code = ExecCode(value = req.code),
            funcId = null,
            correlationId = null,
            invocation = EmptyInvocation
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}
