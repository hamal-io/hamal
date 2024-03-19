package io.hamal.core.request.handler.namespace

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.NamespaceAppendRequested
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceTreeCmdRepository
import io.hamal.repository.api.NamespaceTreeCmdRepository.AppendCmd
import io.hamal.repository.api.NamespaceTreeQueryRepository
import io.hamal.repository.api.event.NamespaceAppendedEvent
import org.springframework.stereotype.Component


@Component
class NamespaceAppendHandler(
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<NamespaceAppendRequested>(NamespaceAppendRequested::class) {

    /**
     * Creates new namespaces on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: NamespaceAppendRequested) {
        createNamespace(req)
            .also { appendNamespace(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createNamespace(req: NamespaceAppendRequested): Namespace {

        val features = NamespaceFeatures.default
        req.features?.let {
            features.value.nodes.putAll(it.value.nodes)
        }

        return namespaceCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                namespaceId = req.id,
                workspaceId = req.workspaceId,
                name = req.name,
                features = features
            )
        )
    }

    private fun appendNamespace(req: NamespaceAppendRequested) {
        val tree = namespaceTreeQueryRepository.get(req.parentId)
        namespaceTreeCmdRepository.append(
            AppendCmd(
                id = req.cmdId(),
                treeId = tree.id,
                parentId = req.parentId,
                namespaceId = req.id
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, namespace: Namespace) {
        eventEmitter.emit(cmdId, NamespaceAppendedEvent(namespace))
    }


}
