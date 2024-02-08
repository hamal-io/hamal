package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceCreateRequest
import io.hamal.lib.domain.request.NamespaceCreateRequested
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequested
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface NamespaceCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: NamespaceCreateRequest,
        responseHandler: (NamespaceCreateRequested) -> T
    ): T
}

interface NamespaceGetPort {
    operator fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (Namespace) -> T): T
}

interface NamespaceListPort {
    operator fun <T : Any> invoke(query: NamespaceQuery, responseHandler: (List<Namespace>) -> T): T
}


interface NamespaceUpdatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: NamespaceUpdateRequest,
        responseHandler: (NamespaceUpdateRequested) -> T
    ): T
}

interface NamespacePort : NamespaceCreatePort, NamespaceGetPort, NamespaceListPort, NamespaceUpdatePort

@Component
class NamespaceAdapter(
    private val generateDomainId: GenerateId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: RequestCmdRepository
) : NamespacePort {

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: NamespaceCreateRequest,
        responseHandler: (NamespaceCreateRequested) -> T
    ): T {
        return NamespaceCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            namespaceId = generateDomainId(::NamespaceId),
            groupId = groupId,
            name = req.name,
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (Namespace) -> T): T =
        responseHandler(namespaceQueryRepository.get(namespaceId))

    override fun <T : Any> invoke(query: NamespaceQuery, responseHandler: (List<Namespace>) -> T): T =
        responseHandler(namespaceQueryRepository.list(query))


    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: NamespaceUpdateRequest,
        responseHandler: (NamespaceUpdateRequested) -> T
    ): T {
        ensureNamespaceExists(namespaceId)
        return NamespaceUpdateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            groupId = namespaceQueryRepository.get(namespaceId).groupId,
            namespaceId = namespaceId,
            name = req.name,
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}