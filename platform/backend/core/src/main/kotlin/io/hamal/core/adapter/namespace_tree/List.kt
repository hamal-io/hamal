package io.hamal.core.adapter.namespace_tree

import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceTreeListPort {
    operator fun invoke(query: NamespaceTreeQueryRepository.NamespaceTreeQuery): List<NamespaceTree>
}

@Component
class NamespaceTreeListAdapter(
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : NamespaceTreeListPort {
    override fun invoke(query: NamespaceTreeQueryRepository.NamespaceTreeQuery): List<NamespaceTree> =
        namespaceTreeQueryRepository.list(query)
}