package io.hamal.core.adapter.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceGetPort {
    operator fun invoke(namespaceId: NamespaceId): Namespace
}

@Component
class NamespaceGetAdapter(
    private val namespaceQueryRepository: NamespaceQueryRepository
) : NamespaceGetPort {
    override fun invoke(namespaceId: NamespaceId): Namespace = namespaceQueryRepository.get(namespaceId)
}
