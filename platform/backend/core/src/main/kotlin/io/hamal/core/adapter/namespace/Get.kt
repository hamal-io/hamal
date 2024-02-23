package io.hamal.core.adapter.namespace

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceGetPort {
    operator fun invoke(namespaceId: NamespaceId): Namespace
}

@Component
class NamespaceGetAdapter(
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : NamespaceGetPort {
    override fun invoke(namespaceId: NamespaceId): Namespace = ensureAccess(
        namespaceQueryRepository.get(namespaceId)
    )
}
