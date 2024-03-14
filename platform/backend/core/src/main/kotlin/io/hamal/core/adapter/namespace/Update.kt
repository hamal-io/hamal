package io.hamal.core.adapter.namespace

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceUpdateRequest
import io.hamal.lib.domain.request.NamespaceUpdateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface NamespaceUpdatePort {
    operator fun invoke(namespaceId: NamespaceId, req: NamespaceUpdateRequest): NamespaceUpdateRequested
}

@Component
class NamespaceUpdateAdapter(
    private val namespaceGet: NamespaceGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : NamespaceUpdatePort {
    override fun invoke(namespaceId: NamespaceId, req: NamespaceUpdateRequest): NamespaceUpdateRequested {
        val namespace = namespaceGet(namespaceId)
        return NamespaceUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            workspaceId = namespace.workspaceId,
            id = namespaceId,
            name = req.name,
            features = req.features
        ).also(requestEnqueue::invoke)
    }

}