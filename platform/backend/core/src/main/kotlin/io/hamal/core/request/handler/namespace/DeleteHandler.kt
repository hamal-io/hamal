package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.NamespaceDeleteRequested
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.event.NamespaceDeletedEvent
import org.springframework.stereotype.Component

@Component
class NamespaceDeleteHandler(
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<NamespaceDeleteRequested>(NamespaceDeleteRequested::class) {
    override fun invoke(req: NamespaceDeleteRequested) {
        deleteNamespace(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun deleteNamespace(req: NamespaceDeleteRequested): Namespace {
        return namespaceCmdRepository.delete(
            req.id, NamespaceCmdRepository.DeleteCmd(
                id = req.cmdId(),
                namespaceId = req.id
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, namespace: Namespace) {
        eventEmitter.emit(cmdId, NamespaceDeletedEvent(namespace))
    }
}