package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.request.AdhocInvokeRequest
import org.springframework.stereotype.Component


interface AdhocInvokePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: AdhocInvokeRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T
}

interface AdhocPort : AdhocInvokePort

@Component
class AdhocAdapter(
    private val generateDomainId: GenerateId,
    private val flowQueryRepository: FlowQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : AdhocPort {
    override operator fun <T : Any> invoke(
        flowId: FlowId,
        req: AdhocInvokeRequest,
        responseHandler: (ExecInvokeRequested) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return ExecInvokeRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            flowId = flow.id,
            groupId = flow.groupId,
            inputs = req.inputs,
            code = ExecCode(value = req.code),
            funcId = null,
            correlationId = null,
            invocation = EmptyInvocation
        ).also(requestCmdRepository::queue).let(responseHandler)
    }
}
