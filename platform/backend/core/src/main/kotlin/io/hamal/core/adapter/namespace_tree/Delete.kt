package io.hamal.core.adapter.namespace_tree

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sdk.api.ApiNamespaceDeleteRequest
import org.springframework.stereotype.Component

fun interface NamespaceDeletePort {
    operator fun invoke(namespaceId: NamespaceId, req: ApiNamespaceDeleteRequest): NamespaceDeleteRequested
}

@Component
class Delete(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : NamespaceDeletePort {
    override fun invoke(namespaceId: NamespaceId, req: ApiNamespaceDeleteRequest): NamespaceDeleteRequested {
        return NamespaceDeleteRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = namespaceId,
            parentId = req.parentId,
        ).also { requestEnqueue::invoke }
    }
}