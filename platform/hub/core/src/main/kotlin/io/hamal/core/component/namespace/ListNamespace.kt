package io.hamal.core.component.namespace

import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

@Component
class ListNamespace(private val namespaceQueryRepository: NamespaceQueryRepository) {
    operator fun <T : Any> invoke(
        query: NamespaceQueryRepository.NamespaceQuery,
        responseHandler: (List<Namespace>) -> T
    ): T = responseHandler(namespaceQueryRepository.list(query))
}