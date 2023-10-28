package io.hamal.core.req.handler.namespace

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.event.NamespaceCreatedEvent
import io.hamal.repository.api.submitted_req.NamespaceUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class UpdateNamespaceHandler(
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<NamespaceUpdateSubmitted>(NamespaceUpdateSubmitted::class) {

    override fun invoke(req: NamespaceUpdateSubmitted) {
        updateNamespace(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateNamespaceHandler.updateNamespace(req: NamespaceUpdateSubmitted): Namespace {
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
