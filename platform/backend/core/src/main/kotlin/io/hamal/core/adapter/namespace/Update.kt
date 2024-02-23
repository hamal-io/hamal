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
            id = generateDomainId(::RequestId),
            by = SecurityContext.currentAuthId,
            status = RequestStatus.Submitted,
            workspaceId = namespace.workspaceId,
            namespaceId = namespaceId,
            name = req.name,
        ).also(requestEnqueue::invoke)
    }

}