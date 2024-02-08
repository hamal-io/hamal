package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.lib.domain.request.EndpointCreateRequested
import io.hamal.lib.domain.request.EndpointUpdateRequested
import io.hamal.lib.domain.request.EndpointCreateRequest
import io.hamal.lib.domain.request.EndpointUpdateRequest
import org.springframework.stereotype.Component

interface EndpointCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: EndpointCreateRequest,
        responseHandler: (EndpointCreateRequested) -> T
    ): T
}

interface EndpointGetPort {
    operator fun <T : Any> invoke(endpointId: EndpointId, responseHandler: (Endpoint, Func) -> T): T
}

interface EndpointListPort {
    operator fun <T : Any> invoke(query: EndpointQuery, responseHandler: (List<Endpoint>, Map<FuncId, Func>) -> T): T
}

interface EndpointUpdatePort {
    operator fun <T : Any> invoke(
        endpointId: EndpointId,
        req: EndpointUpdateRequest,
        responseHandler: (EndpointUpdateRequested) -> T
    ): T
}

interface EndpointPort : EndpointCreatePort, EndpointGetPort, EndpointListPort, EndpointUpdatePort

@Component
class EndpointAdapter(
    private val generateDomainId: GenerateId,
    private val endpointQueryRepository: EndpointQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val reqCmdRepository: RequestCmdRepository
) : EndpointPort {
    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: EndpointCreateRequest,
        responseHandler: (EndpointCreateRequested) -> T
    ): T {
        val func = funcQueryRepository.get(req.funcId)
        require(namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        return EndpointCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            endpointId = generateDomainId(::EndpointId),
            groupId = func.groupId,
            funcId = func.id,
            name = req.name,
            method = req.method
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(endpointId: EndpointId, responseHandler: (Endpoint, Func) -> T): T {
        val endpoint = endpointQueryRepository.get(endpointId)
        val funcs = funcQueryRepository.get(endpoint.funcId)
        return responseHandler(endpoint, funcs)
    }

    override fun <T : Any> invoke(
        query: EndpointQuery,
        responseHandler: (List<Endpoint>, Map<FuncId, Func>) -> T
    ): T {
        val endpoints = endpointQueryRepository.list(query)
        val funcs = funcQueryRepository.list(endpoints.map(Endpoint::funcId))
            .associateBy(Func::id)
        return responseHandler(endpoints, funcs)
    }

    override fun <T : Any> invoke(
        endpointId: EndpointId,
        req: EndpointUpdateRequest,
        responseHandler: (EndpointUpdateRequested) -> T
    ): T {
        val endpoint = endpointQueryRepository.get(endpointId)

        req.funcId?.let { funcId ->
            val func = funcQueryRepository.get(funcId)
            require(endpoint.namespaceId == func.namespaceId) { "Endpoint and Func must share the same Namespace" }
        }

        return EndpointUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            groupId = endpoint.groupId,
            endpointId = endpointId,
            funcId = req.funcId ?: endpoint.funcId,
            name = req.name,
            method = req.method
        ).also(reqCmdRepository::queue).let(responseHandler)
    }
}