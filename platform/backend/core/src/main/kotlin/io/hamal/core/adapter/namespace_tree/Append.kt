package io.hamal.core.adapter.namespace_tree

import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceAppendRequest
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface NamespaceTreeAppendPort {
    operator fun invoke(parentId: NamespaceId, req: NamespaceAppendRequest): NamespaceAppendRequested
}

@Component
class NamespaceTreeAppendAdapter(
    private val namespaceGet: NamespaceGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : NamespaceTreeAppendPort {
    override fun invoke(parentId: NamespaceId, req: NamespaceAppendRequest): NamespaceAppendRequested {
        val parent = namespaceGet(parentId)
        return NamespaceAppendRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            parentId = parent.id,
            id = generateDomainId(::NamespaceId),
            workspaceId = parent.workspaceId,
            name = req.name,
            features = req.features
        ).also(requestEnqueue::invoke)
    }

}