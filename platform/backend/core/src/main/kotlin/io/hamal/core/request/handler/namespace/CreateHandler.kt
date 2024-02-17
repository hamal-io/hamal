package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.request.NamespaceCreateRequested
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.NamespaceCreatedEvent
import org.springframework.stereotype.Component


@Component
class NamespaceCreateHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val namespaceQueryRepository: NamespaceQueryRepository,
    val eventEmitter: InternalEventEmitter,
    val generateDomainId: GenerateId
) : io.hamal.core.request.RequestHandler<NamespaceCreateRequested>(NamespaceCreateRequested::class) {

    /**
     * Creates new namespaces on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: NamespaceCreateRequested) {
        createNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun NamespaceCreateHandler.createNamespace(req: NamespaceCreateRequested): Namespace {
    val parentId = namespaceQueryRepository.get(req.parentId)
    return namespaceCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            parentId = req.parentId,
            namespaceId = req.namespaceId,
            workspaceId = parentId.workspaceId,
            name = req.name
        )
    )
}

private fun NamespaceCreateHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceCreatedEvent(namespace))
}
