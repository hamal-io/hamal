package io.hamal.core.request.handler.hook

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import io.hamal.repository.api.event.HookCreatedEvent
import org.springframework.stereotype.Component

object Keep

@Component
class HookCreateHandler(
    val hookCmdRepository: HookCmdRepository,
    val eventEmitter: PlatformEventEmitter,
    val flowQueryRepository: FlowQueryRepository
) : io.hamal.core.request.RequestHandler<HookCreateRequested>(HookCreateRequested::class) {
    override fun invoke(req: HookCreateRequested) {
        createHook(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun HookCreateHandler.createHook(req: HookCreateRequested): Hook {
    return hookCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            hookId = req.hookId,
            groupId = req.groupId,
            flowId = req.flowId,
            name = req.name
        )
    )
}

private fun HookCreateHandler.emitEvent(cmdId: CmdId, hook: Hook) {
    eventEmitter.emit(cmdId, HookCreatedEvent(hook))
}