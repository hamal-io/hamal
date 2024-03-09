package io.hamal.core.request.handler.hook

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.HookCreatedEvent
import org.springframework.stereotype.Component

@Component
class HookCreateHandler(
    private val hookCmdRepository: HookCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : RequestHandler<HookCreateRequested>(HookCreateRequested::class) {

    override fun invoke(req: HookCreateRequested) {
        createHook(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createHook(req: HookCreateRequested): Hook {
        return hookCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                hookId = req.id,
                workspaceId = req.workspaceId,
                namespaceId = req.namespaceId,
                name = req.name
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, hook: Hook) {
        eventEmitter.emit(cmdId, HookCreatedEvent(hook))
    }

}

