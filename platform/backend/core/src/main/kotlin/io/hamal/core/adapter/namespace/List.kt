package io.hamal.core.adapter.namespace

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceListPort {
    operator fun invoke(query: NamespaceQueryRepository.NamespaceQuery): List<Namespace>
}

@Component
class NamespaceListAdapter(
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val filterAccess: FilterAccessPort
) : NamespaceListPort {
    override fun invoke(query: NamespaceQueryRepository.NamespaceQuery): List<Namespace> = filterAccess(namespaceQueryRepository.list(query))
}
