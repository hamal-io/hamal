package io.hamal.core.req.handler.hook

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.event.HookCreatedEvent
import io.hamal.repository.api.submitted_req.HookCreateSubmitted
import org.springframework.stereotype.Component

@Component
class CreateHookHandler(
    val hookCmdRepository: HookCmdRepository,
    val eventEmitter: PlatformEventEmitter,
    val namespaceQueryRepository: NamespaceQueryRepository
) : ReqHandler<HookCreateSubmitted>(HookCreateSubmitted::class) {
    override fun invoke(req: HookCreateSubmitted) {
        createHook(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateHookHandler.createHook(req: HookCreateSubmitted): Hook {
    return hookCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            hookId = req.id,
            groupId = req.groupId,
            namespaceId = req.namespaceId ?: namespaceQueryRepository.get(NamespaceName("hamal")).id,
            name = req.name
        )
    )
}

private fun CreateHookHandler.emitEvent(cmdId: CmdId, hook: Hook) {
    eventEmitter.emit(cmdId, HookCreatedEvent(hook))
}
