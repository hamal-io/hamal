package io.hamal.core.req.handler.hook

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository.UpdateCmd
import io.hamal.repository.api.HookRepository
import io.hamal.repository.api.event.HookCreatedEvent
import org.springframework.stereotype.Component


@Component
class HookUpdateHandler(
    val hookRepository: HookRepository, val eventEmitter: PlatformEventEmitter
) : ReqHandler<HookUpdateRequested>(HookUpdateRequested::class) {

    override fun invoke(req: HookUpdateRequested) {
        updateHook(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun HookUpdateHandler.updateHook(req: HookUpdateRequested): Hook {
    return hookRepository.update(
        req.hookId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
        )
    )
}

private fun HookUpdateHandler.emitEvent(cmdId: CmdId, hook: Hook) {
    eventEmitter.emit(cmdId, HookCreatedEvent(hook))
}
