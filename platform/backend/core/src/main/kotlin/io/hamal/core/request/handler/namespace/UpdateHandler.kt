package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.NamespaceUpdateRequested
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.event.NamespaceUpdatedEvent
import org.springframework.stereotype.Component


@Component
class NamespaceUpdateHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: InternalEventEmitter
) : io.hamal.core.request.RequestHandler<NamespaceUpdateRequested>(NamespaceUpdateRequested::class) {

    override fun invoke(req: NamespaceUpdateRequested) {
        updateNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun NamespaceUpdateHandler.updateNamespace(req: NamespaceUpdateRequested): Namespace {
    return namespaceCmdRepository.update(
        req.namespaceId,
        NamespaceCmdRepository.UpdateCmd(
            id = req.cmdId(),
            name = req.name
        )
    )
}

private fun NamespaceUpdateHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceUpdatedEvent(namespace))
}
