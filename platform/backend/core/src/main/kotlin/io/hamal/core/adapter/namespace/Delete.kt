package io.hamal.core.adapter.namespace

import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface NamespaceDeletePort {
    operator fun invoke(namespaceId: NamespaceId): NamespaceDeleteRequested
}

@Component
class NamespaceDeleteAdapter(
    private val generateDomainId: GenerateDomainId,
    private val getSubTree: NamespaceTreeGetSubTreePort,
    private val requestEnqueue: RequestEnqueuePort
) : NamespaceDeletePort {
    override fun invoke(namespaceId: NamespaceId): NamespaceDeleteRequested {
        val parent = getSubTree(namespaceId)

        return NamespaceDeleteRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = namespaceId,
            parentId = parent.root.value,
        ).also { requestEnqueue::invoke }
    }
}