package io.hamal.core.req.handler.namespace

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.NamespaceCreatedEvent
import io.hamal.repository.api.submitted_req.NamespaceCreateSubmittedReq
import org.springframework.stereotype.Component

@Component
class CreateNamespaceHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val namespaceQueryRepository: NamespaceQueryRepository,
    val eventEmitter: PlatformEventEmitter,
    val generateDomainId: GenerateDomainId
) : ReqHandler<NamespaceCreateSubmittedReq>(NamespaceCreateSubmittedReq::class) {

    /**
     * Creates new namespaces on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: NamespaceCreateSubmittedReq) {
        createNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateNamespaceHandler.createNamespace(req: NamespaceCreateSubmittedReq): Namespace {
    val existingNamespaces = namespaceQueryRepository.list(
        NamespaceQueryRepository.NamespaceQuery(
            groupIds = listOf(req.groupId),
            limit = Limit.all
        )
    )
    existingNamespaces.find { it.name == req.name }?.let { return it }

    val existingNamespaceNames = existingNamespaces.map(Namespace::name)

    val allNames = req.name.allNamespaceNames()
    allNames.take(allNames.size - 1).filter { name -> !existingNamespaceNames.contains(name) }.forEach { name ->
        namespaceCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                namespaceId = generateDomainId(::NamespaceId),
                groupId = req.groupId,
                name = name,
                inputs = req.inputs
            )
        )
    }

    return namespaceCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            namespaceId = req.id,
            groupId = req.groupId,
            name = req.name,
            inputs = req.inputs
        )
    )
}

private fun CreateNamespaceHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceCreatedEvent(namespace))
}
