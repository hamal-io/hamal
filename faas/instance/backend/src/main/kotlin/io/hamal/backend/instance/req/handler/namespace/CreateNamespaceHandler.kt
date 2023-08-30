package io.hamal.backend.instance.req.handler.namespace

import io.hamal.backend.instance.event.NamespaceCreatedEvent
import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedCreateNamespaceReq
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.NamespaceId
import org.springframework.stereotype.Component

@Component
class CreateNamespaceHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val namespaceQueryRepository: NamespaceQueryRepository,
    val eventEmitter: InstanceEventEmitter,
    val generateDomainId: GenerateDomainId
) : ReqHandler<SubmittedCreateNamespaceReq>(SubmittedCreateNamespaceReq::class) {

    /**
     * Creates new namespaces on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: SubmittedCreateNamespaceReq) {
        createNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateNamespaceHandler.createNamespace(req: SubmittedCreateNamespaceReq): Namespace {
    val existingNamespaces = namespaceQueryRepository.list { limit = Limit(Int.MAX_VALUE) }
    existingNamespaces.find { it.name == req.name }?.let { return it }

    val existingNamespaceNames = existingNamespaces.map(Namespace::name)

    val allNames = req.name.allNamespaceNames()
    allNames.take(allNames.size - 1).filter { name -> !existingNamespaceNames.contains(name) }.forEach { name ->
        namespaceCmdRepository.create(CreateCmd(req.cmdId(), generateDomainId(::NamespaceId), name, req.inputs))
    }

    return namespaceCmdRepository.create(
        CreateCmd(req.cmdId(), req.id, req.name, req.inputs)
    )
}

private fun CreateNamespaceHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceCreatedEvent(namespace))
}
