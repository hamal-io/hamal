package io.hamal.backend.instance.req.handler.namespace

import io.hamal.backend.instance.event.NamespaceCreatedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedUpdateNamespaceReq
import io.hamal.lib.common.domain.CmdId
import org.springframework.stereotype.Component

@Component
class UpdateNamespaceHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: SystemEventEmitter
) : ReqHandler<SubmittedUpdateNamespaceReq>(SubmittedUpdateNamespaceReq::class) {

    override fun invoke(req: SubmittedUpdateNamespaceReq) {
        updateNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateNamespaceHandler.updateNamespace(req: SubmittedUpdateNamespaceReq): Namespace {
    return namespaceCmdRepository.update(
        req.id,
        NamespaceCmdRepository.UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
        )
    )
}

private fun UpdateNamespaceHandler.emitEvent(cmdId: CmdId, namespace: Namespace) {
    eventEmitter.emit(cmdId, NamespaceCreatedEvent(namespace))
}
