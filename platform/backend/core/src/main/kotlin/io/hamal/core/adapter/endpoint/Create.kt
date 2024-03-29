package io.hamal.core.adapter.endpoint

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
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
    private val funcGet: FuncGetPort,
    private val namespaceGet: NamespaceGetPort
) : EndpointCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: EndpointCreateRequest): EndpointCreateRequested {
        val namespace = namespaceGet(namespaceId)
        val func = funcGet(req.funcId)
        require(namespace.id == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        return EndpointCreateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = generateDomainId(::EndpointId),
            workspaceId = func.workspaceId,
            funcId = func.id,
            name = req.name,
            method = req.method
        ).also(requestEnqueue::invoke)
    }

}
