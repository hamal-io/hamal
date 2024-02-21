package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.EndpointCreateRequest
import io.hamal.lib.domain.request.EndpointCreateRequested
import io.hamal.lib.domain.request.EndpointUpdateRequest
import io.hamal.lib.domain.request.EndpointUpdateRequested
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointQueryRepository
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface EndpointCreatePort {
    operator fun invoke(
        namespaceId: NamespaceId,
        req: EndpointCreateRequest
    ): EndpointCreateRequested
}

interface EndpointGetPort {
    operator fun invoke(endpointId: EndpointId): Endpoint
}

interface EndpointListPort {
    operator fun invoke(query: EndpointQuery): List<Endpoint>
}

interface EndpointUpdatePort {
    operator fun invoke(
        endpointId: EndpointId,
        req: EndpointUpdateRequest
    ): EndpointUpdateRequested
}

interface EndpointPort : EndpointCreatePort, EndpointGetPort, EndpointListPort, EndpointUpdatePort

@Component
class EndpointAdapter(
    private val generateDomainId: GenerateId,
    private val endpointQueryRepository: EndpointQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : EndpointPort {
    override fun invoke(
        namespaceId: NamespaceId,
        req: EndpointCreateRequest,
    ): EndpointCreateRequested {
        val func = funcQueryRepository.get(req.funcId)
        require(namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        return EndpointCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            endpointId = generateDomainId(::EndpointId),
            workspaceId = func.workspaceId,
            funcId = func.id,
            name = req.name,
            method = req.method
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(endpointId: EndpointId): Endpoint = endpointQueryRepository.get(endpointId)

    override fun invoke(
        query: EndpointQuery
    ): List<Endpoint> = endpointQueryRepository.list(query)

    override fun invoke(
        endpointId: EndpointId,
        req: EndpointUpdateRequest
    ): EndpointUpdateRequested {
        val endpoint = endpointQueryRepository.get(endpointId)

        req.funcId?.let { funcId ->
            val func = funcQueryRepository.get(funcId)
            require(endpoint.namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        }

        return EndpointUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = endpoint.workspaceId,
            endpointId = endpointId,
            funcId = req.funcId ?: endpoint.funcId,
            name = req.name,
            method = req.method
        ).also(requestCmdRepository::queue)
    }
}