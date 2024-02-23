package io.hamal.core.adapter.namespace_tree

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.NamespaceSubTree
import io.hamal.repository.api.NamespaceTreeQueryRepository
import org.springframework.stereotype.Component

fun interface NamespaceTreeGetSubTreePort {
    operator fun invoke(namespaceId: NamespaceId): NamespaceSubTree
}

@Component
class NamespaceTreeGetSubTreeAdapter(
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : NamespaceTreeGetSubTreePort {
    override fun invoke(namespaceId: NamespaceId): NamespaceSubTree {
        return namespaceTreeQueryRepository.get(namespaceId).let { tree ->
            NamespaceSubTree(
                cmdId = tree.cmdId,
                id = tree.id,
                workspaceId = tree.workspaceId,
                root = tree.root.get { it.value == namespaceId }
            )
        }
    }
}
