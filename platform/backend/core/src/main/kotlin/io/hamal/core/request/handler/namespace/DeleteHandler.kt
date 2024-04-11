package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceTreeCmdRepository
import io.hamal.repository.api.NamespaceTreeQueryRepository
import io.hamal.repository.api.event.NamespaceDeletedEvent
import org.springframework.stereotype.Component

@Component
class NamespaceDeleteHandler(
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<NamespaceDeleteRequested>(NamespaceDeleteRequested::class) {
    override fun invoke(req: NamespaceDeleteRequested) {
        deleteNamespace(req).also { emitEvent(req.cmdId(), it) }
    }

    private fun deleteNamespace(req: NamespaceDeleteRequested): Namespace {
        return namespaceCmdRepository.delete(
            req.id,
            NamespaceCmdRepository.DeleteCmd(
                id = req.cmdId(),
                namespaceId = req.id
            )
        )
    }

    /*
        private fun reduceTree(req: NamespaceDeleteRequested) {
            val tree = namespaceTreeQueryRepository.get(req.parentId)
            namespaceTreeCmdRepository.reduce(
                NamespaceTreeCmdRepository.DeleteCmd(
                    id = req.cmdId(),
                    treeId = tree.id,
                    parentId = req.parentId,
                    namespaceId = req.id
                )
            )
        }
    */

    private fun emitEvent(cmdId: CmdId, namespace: Namespace) {
        eventEmitter.emit(cmdId, NamespaceDeletedEvent(namespace))
    }
}