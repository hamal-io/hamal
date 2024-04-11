package io.hamal.core.event.handler.namespace

import io.hamal.core.adapter.namespace.NamespaceDeletePort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.event.InternalEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeRepository
import io.hamal.repository.api.event.NamespaceDeletedEvent
import org.springframework.stereotype.Component

@Component
internal class NamespaceDeletedHandler(
    private val namespaceRepository: NamespaceRepository,
    private val namespaceTreeRepository: NamespaceTreeRepository,
    private val getSubTree: NamespaceTreeGetSubTreePort,
    private val deleteNamespace: NamespaceDeletePort
) : InternalEventHandler<NamespaceDeletedEvent> {

    override fun handle(cmdId: CmdId, evt: NamespaceDeletedEvent) {
        val tree: NamespaceTree? = namespaceTreeRepository.find(evt.namespace.id)

        val children = getSubTree(evt.namespace.id).values
        children.forEach {
            deleteNamespace(it)
        }

        //Delete Tree Node...

    }
}