package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.NamespaceCreateSubmitted
import io.hamal.repository.api.submitted_req.NamespaceUpdateSubmitted
import io.hamal.request.CreateNamespaceReq
import io.hamal.request.UpdateNamespaceReq
import org.springframework.stereotype.Component

interface NamespaceCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateNamespaceReq,
        responseHandler: (NamespaceCreateSubmitted) -> T
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
        req: UpdateNamespaceReq,
        responseHandler: (NamespaceUpdateSubmitted) -> T
    ): T
}

interface NamespacePort : NamespaceCreatePort, NamespaceGetPort, NamespaceListPort, NamespaceUpdatePort

@Component
class NamespaceAdapter(
    private val generateDomainId: GenerateDomainId,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : NamespacePort {

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateNamespaceReq,
        responseHandler: (NamespaceCreateSubmitted) -> T
    ): T {
        return NamespaceCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            namespaceId = generateDomainId(::NamespaceId),
            groupId = groupId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(namespaceId: NamespaceId, responseHandler: (Namespace) -> T): T =
        responseHandler(namespaceQueryRepository.get(namespaceId))

    override fun <T : Any> invoke(query: NamespaceQuery, responseHandler: (List<Namespace>) -> T): T =
        responseHandler(namespaceQueryRepository.list(query))


    override operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: UpdateNamespaceReq,
        responseHandler: (NamespaceUpdateSubmitted) -> T
    ): T {
        ensureNamespaceExists(namespaceId)
        return NamespaceUpdateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = namespaceQueryRepository.get(namespaceId).groupId,
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}