package io.hamal.core.event.handler.namespace

import io.hamal.core.adapter.namespace.NamespaceDeletePort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.event.InternalEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.event.NamespaceDeletedEvent
import org.springframework.stereotype.Component

@Component
internal class NamespaceDeletedHandler(
    private val getSubTree: NamespaceTreeGetSubTreePort,
    private val deleteNamespace: NamespaceDeletePort,
) : InternalEventHandler<NamespaceDeletedEvent> {

    override fun handle(cmdId: CmdId, evt: NamespaceDeletedEvent) {
        val subTree = getSubTree(evt.namespace.id)

        if (subTree.values.isNotEmpty()) {
            subTree.values.forEach {
                deleteNamespace(it)
            }
        }
    }
}


