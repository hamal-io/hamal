package io.hamal.core.component.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

@Component
class GetNamespace(private val namespaceQueryRepository: NamespaceQueryRepository) {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        responseHandler: (Namespace) -> T
    ): T = responseHandler(namespaceQueryRepository.get(namespaceId))
}