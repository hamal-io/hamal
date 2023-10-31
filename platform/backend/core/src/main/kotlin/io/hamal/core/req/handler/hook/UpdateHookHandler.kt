package io.hamal.core.req.handler.hook

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository.UpdateCmd
import io.hamal.repository.api.HookRepository
import io.hamal.repository.api.event.HookCreatedEvent
import io.hamal.repository.api.submitted_req.HookUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class UpdateHookHandler(
    val hookRepository: HookRepository, val eventEmitter: PlatformEventEmitter
) : ReqHandler<HookUpdateSubmitted>(HookUpdateSubmitted::class) {

    override fun invoke(req: HookUpdateSubmitted) {
        updateHook(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateHookHandler.updateHook(req: HookUpdateSubmitted): Hook {
    return hookRepository.update(
        req.hookId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
        )
    )
}

private fun UpdateHookHandler.emitEvent(cmdId: CmdId, hook: Hook) {
    eventEmitter.emit(cmdId, HookCreatedEvent(hook))
}
