package io.hamal.core.adapter.namespace

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.NamespaceTreeQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceDeletePort {
    operator fun invoke(namespaceId: NamespaceId): NamespaceDeleteRequested
}

@Component
class NamespaceDeleteAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : NamespaceDeletePort {
    override fun invoke(namespaceId: NamespaceId): NamespaceDeleteRequested {
        val root = namespaceTreeQueryRepository.get(namespaceId).root
        if (root.value == namespaceId) {
            throw IllegalArgumentException("Tried to delete root namespace")
        }
        return NamespaceDeleteRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = namespaceId,
        ).also(requestEnqueue::invoke)
    }
}