package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateNamespaceReq
import io.hamal.request.UpdateNamespaceReq
import org.springframework.stereotype.Component

interface CreateNamespacePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateNamespaceReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetNamespacePort {
    operator fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (Namespace) -> T): T
}

interface ListNamespacesPort {
    operator fun <T : Any> invoke(query: NamespaceQuery, responseHandler: (List<Namespace>) -> T): T
}


interface UpdateNamespacePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: UpdateNamespaceReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface NamespacePort : CreateNamespacePort, GetNamespacePort, ListNamespacesPort, UpdateNamespacePort


@Component
class NamespaceAdapter(
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val submitRequest: SubmitRequest,
) : NamespacePort {

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateNamespaceReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T =
        responseHandler(submitRequest(groupId, req))

    override fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (Namespace) -> T): T =
        responseHandler(namespaceQueryRepository.get(namespaceId))

    override fun <T : Any> invoke(query: NamespaceQuery, responseHandler: (List<Namespace>) -> T): T =
        responseHandler(namespaceQueryRepository.list(query))

    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: UpdateNamespaceReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        ensureNamespaceExists(namespaceId)
        return responseHandler(submitRequest(namespaceId, req))
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}