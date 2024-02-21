package io.hamal.core.adapter.endpoint

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.EndpointUpdateRequest
import io.hamal.lib.domain.request.EndpointUpdateRequested
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface EndpointUpdatePort {
    operator fun invoke(endpointId: EndpointId, req: EndpointUpdateRequest): EndpointUpdateRequested
}

@Component
class EndpointUpdateAdapter(
    private val endpointGet: EndpointGetPort,
    private val funcGet: FuncGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : EndpointUpdatePort {
    override fun invoke(endpointId: EndpointId, req: EndpointUpdateRequest): EndpointUpdateRequested {
        val endpoint = endpointGet(endpointId)

        req.funcId?.let { funcId ->
            val func = funcGet(funcId)
            require(endpoint.namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        }

        return EndpointUpdateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = endpoint.workspaceId,
            endpointId = endpointId,
            funcId = req.funcId ?: endpoint.funcId,
            name = req.name,
            method = req.method
        ).also(requestEnqueue::invoke)
    }

}