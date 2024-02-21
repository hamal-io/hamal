package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.repository.api.*
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceTreeCmdRepository.AppendCmd
import io.hamal.repository.api.event.NamespaceAppendedEvent
import org.springframework.stereotype.Component


@Component
class NamespaceAppendHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    val namespaceQueryRepository: NamespaceQueryRepository,
    val namespaceTreeQueryRepository: NamespaceTreeQueryRepository,
    val eventEmitter: InternalEventEmitter,
    val generateDomainId: GenerateDomainId
) : RequestHandler<NamespaceAppendRequested>(NamespaceAppendRequested::class) {

    /**
     * Creates new namespaces on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: NamespaceAppendRequested) {
        createNamespace(req)
            .also { appendNamespace(req) }
            .also { emitEvent(req.cmdId(), it) }
    }
}

private fun NamespaceAppendHandler.createNamespace(req: NamespaceAppendRequested): Namespace {
    return namespaceCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            namespaceId = req.namespaceId,
            workspaceId = req.workspaceId,
            name = req.name
        )
    )
}

private fun NamespaceAppendHandler.appendNamespace(req: NamespaceAppendRequested) {
    val tree = namespaceTreeQueryRepository.get(req.parentId)
    namespaceTreeCmdRepository.append(
        AppendCmd(
            id = req.cmdId(),
            treeId = tree.id,
            parentId = req.parentId,
            namespaceId = req.namespaceId
        )
    )
}

private fun NamespaceAppendHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceAppendedEvent(namespace))
}
