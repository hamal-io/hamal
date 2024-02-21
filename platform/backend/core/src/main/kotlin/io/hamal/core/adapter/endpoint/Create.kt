package io.hamal.core.adapter.endpoint

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.EndpointCreateRequest
import io.hamal.lib.domain.request.EndpointCreateRequested
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface EndpointCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: EndpointCreateRequest): EndpointCreateRequested
}

@Component
class EndpointCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val funcGet: FuncGetPort
) : EndpointCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: EndpointCreateRequest): EndpointCreateRequested {
        val func = funcGet(req.funcId)
        require(namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        return EndpointCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            endpointId = generateDomainId(::EndpointId),
            workspaceId = func.workspaceId,
            funcId = func.id,
            name = req.name,
            method = req.method
        ).also(requestEnqueue::invoke)
    }

}
