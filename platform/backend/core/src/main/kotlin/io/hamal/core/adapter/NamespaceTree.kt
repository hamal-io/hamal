package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.request.NamespaceCreateRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import org.springframework.stereotype.Component

interface NamespaceTreeAppendPort {
    operator fun <T : Any> invoke(
        parentId: NamespaceId, req: NamespaceCreateRequest, responseHandler: (NamespaceAppendRequested) -> T
    ): T
}

interface NamespaceTreeGetSubTreePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId, responseHandler: (NamespaceSubTree) -> T
    ): T
}

interface NamespaceTreeListPort {
    operator fun <T : Any> invoke(query: NamespaceTreeQuery, responseHandler: (List<NamespaceTree>) -> T): T
}


interface NamespaceTreePort : NamespaceTreeAppendPort, NamespaceTreeGetSubTreePort, NamespaceTreeListPort

@Component
class NamespaceTreeAdapter(
    private val generateDomainId: GenerateId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : NamespaceTreePort {

    override fun <T : Any> invoke(
        parentId: NamespaceId, req: NamespaceCreateRequest, responseHandler: (NamespaceAppendRequested) -> T
    ): T {
        val parent = namespaceQueryRepository.get(parentId)
        return NamespaceAppendRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            parentId = parent.id,
            namespaceId = generateDomainId(::NamespaceId),
            workspaceId = parent.workspaceId,
            name = req.name,
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (tree: NamespaceSubTree) -> T): T =
        responseHandler(
            namespaceTreeQueryRepository.get(namespaceId).let { tree ->
                NamespaceSubTree(
                    cmdId = tree.cmdId,
                    id = tree.id,
                    workspaceId = tree.workspaceId,
                    root = tree.root.get { it.value == namespaceId }
                )
            }
        )

    override fun <T : Any> invoke(query: NamespaceTreeQuery, responseHandler: (trees: List<NamespaceTree>) -> T): T =
        responseHandler(namespaceTreeQueryRepository.list(query))
}