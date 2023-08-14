package io.hamal.backend.instance.req.handler.namespace

import io.hamal.backend.instance.event.NamespaceCreatedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.Namespace
import io.hamal.backend.repository.api.NamespaceCmdRepository
import io.hamal.backend.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.backend.repository.api.submitted_req.SubmittedCreateNamespaceReq
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.NamespaceId
import org.springframework.stereotype.Component

@Component
class CreateNamespaceHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: SystemEventEmitter,
    val generateDomainId: GenerateDomainId
) : ReqHandler<SubmittedCreateNamespaceReq>(SubmittedCreateNamespaceReq::class) {
    override fun invoke(req: SubmittedCreateNamespaceReq) {
        createNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateNamespaceHandler.createNamespace(req: SubmittedCreateNamespaceReq): Namespace {
    val allNames = req.name.allNamespaceNames()
    allNames.take(allNames.size - 1).forEach { name ->
        namespaceCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                namespaceId = generateDomainId(::NamespaceId),
                name = name,
                inputs = req.inputs
            )
        )
    }

    return namespaceCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            namespaceId = req.id,
            name = req.name,
            inputs = req.inputs
        )
    )
}

private fun CreateNamespaceHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceCreatedEvent(namespace))
}
