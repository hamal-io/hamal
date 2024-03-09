package io.hamal.core.request.handler.hook

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository.UpdateCmd
import io.hamal.repository.api.HookRepository
import io.hamal.repository.api.event.HookCreatedEvent
import org.springframework.stereotype.Component


@Component
class HookUpdateHandler(
    private val hookRepository: HookRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<HookUpdateRequested>(HookUpdateRequested::class) {

    override fun invoke(req: HookUpdateRequested) {
        updateHook(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateHook(req: HookUpdateRequested): Hook {
        return hookRepository.update(
            req.id, UpdateCmd(
                id = req.cmdId(),
                name = req.name,
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, hook: Hook) {
        eventEmitter.emit(cmdId, HookCreatedEvent(hook))
    }

}

