package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceAppendRequest
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import org.springframework.stereotype.Component

interface NamespaceTreeAppendPort {
    operator fun invoke(parentId: NamespaceId, req: NamespaceAppendRequest): NamespaceAppendRequested
}

interface NamespaceTreeGetSubTreePort {
    operator fun invoke(namespaceId: NamespaceId): NamespaceSubTree
}

interface NamespaceTreeListPort {
    operator fun invoke(query: NamespaceTreeQuery): List<NamespaceTree>
}

interface NamespaceTreePort : NamespaceTreeAppendPort, NamespaceTreeGetSubTreePort, NamespaceTreeListPort

@Component
class NamespaceTreeAdapter(
    private val generateDomainId: GenerateDomainId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : NamespaceTreePort {

    override fun invoke(
        parentId: NamespaceId, req: NamespaceAppendRequest
    ): NamespaceAppendRequested {
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

    override fun invoke(namespaceId: NamespaceId): NamespaceSubTree =
        namespaceTreeQueryRepository.get(namespaceId).let { tree ->
            NamespaceSubTree(
                cmdId = tree.cmdId,
                id = tree.id,
                workspaceId = tree.workspaceId,
                root = tree.root.get { it.value == namespaceId }
            )
        }

    override fun invoke(query: NamespaceTreeQuery): List<NamespaceTree> = namespaceTreeQueryRepository.list(query)
}