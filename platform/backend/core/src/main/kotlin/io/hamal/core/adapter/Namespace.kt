package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.request.NamespaceAppendRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface NamespaceCreatePort {
    operator fun invoke(parentId: NamespaceId, req: NamespaceAppendRequest): NamespaceAppendRequested
}

interface NamespaceGetPort {
    operator fun invoke(namespaceId: NamespaceId): Namespace
}

interface NamespaceListPort {
    operator fun invoke(query: NamespaceQuery): List<Namespace>
}


interface NamespaceUpdatePort {
    operator fun invoke(namespaceId: NamespaceId, req: NamespaceUpdateRequest): NamespaceUpdateRequested
}

interface NamespacePort : NamespaceCreatePort, NamespaceGetPort, NamespaceListPort, NamespaceUpdatePort

@Component
class NamespaceAdapter(
    private val generateDomainId: GenerateId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : NamespacePort {

    override fun invoke(parentId: NamespaceId, req: NamespaceAppendRequest): NamespaceAppendRequested {
        val parent = namespaceQueryRepository.get(parentId)
        return NamespaceAppendRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            parentId = parent.id,
            namespaceId = generateDomainId(::NamespaceId),
            workspaceId = parent.workspaceId,
            name = req.name,
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(namespaceId: NamespaceId): Namespace = namespaceQueryRepository.get(namespaceId)

    override fun invoke(query: NamespaceQuery): List<Namespace> = namespaceQueryRepository.list(query)

    override operator fun invoke(namespaceId: NamespaceId, req: NamespaceUpdateRequest): NamespaceUpdateRequested {
        ensureNamespaceExists(namespaceId)
        return NamespaceUpdateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = namespaceQueryRepository.get(namespaceId).workspaceId,
            namespaceId = namespaceId,
            name = req.name,
        ).also(requestCmdRepository::queue)
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}