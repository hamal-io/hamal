package io.hamal.core.request.handler.blueprint

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.api.event.BlueprintUpdatedEvent
import org.springframework.stereotype.Component

@Component
class BlueprintUpdateHandler(
    val blueprintRepository: BlueprintRepository,
    val eventEmitter: InternalEventEmitter
) : RequestHandler<BlueprintUpdateRequested>(BlueprintUpdateRequested::class) {

    override fun invoke(req: BlueprintUpdateRequested) {
        updateBlueprint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun BlueprintUpdateHandler.updateBlueprint(req: BlueprintUpdateRequested): Blueprint {
    return blueprintRepository.update(
        req.blueprintId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            description = req.description
        )
    )
}

private fun BlueprintUpdateHandler.emitEvent(cmdId: CmdId, bp: Blueprint) {
    eventEmitter.emit(cmdId, BlueprintUpdatedEvent(bp))
}