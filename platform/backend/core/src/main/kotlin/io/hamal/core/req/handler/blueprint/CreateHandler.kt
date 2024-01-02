package io.hamal.core.req.handler.blueprint

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.submitted.BlueprintCreateSubmitted
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository
import io.hamal.repository.api.event.BlueprintCreatedEvent
import org.springframework.stereotype.Component

@Component
class BlueprintCreateHandler(
    val blueprintCmdRepository: BlueprintCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<BlueprintCreateSubmitted>(BlueprintCreateSubmitted::class) {
    override fun invoke(req: BlueprintCreateSubmitted) {
        createBlueprint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun BlueprintCreateHandler.createBlueprint(req: BlueprintCreateSubmitted): Blueprint {
    return blueprintCmdRepository.create(
        BlueprintCmdRepository.CreateCmd(
            id = req.cmdId(),
            blueprintId = req.blueprintId,
            groupId = req.groupId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = req.creatorId
        )
    )
}

private fun BlueprintCreateHandler.emitEvent(cmdId: CmdId, blueprint: Blueprint) {
    eventEmitter.emit(cmdId, BlueprintCreatedEvent(blueprint))
}