package io.hamal.core.req.handler.blueprint

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.submitted.BlueprintUpdateSubmitted
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.api.event.BlueprintUpdatedEvent
import org.springframework.stereotype.Component

@Component
class BlueprintUpdateHandler(
    val blueprintRepository: BlueprintRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<BlueprintUpdateSubmitted>(BlueprintUpdateSubmitted::class) {

    override fun invoke(req: BlueprintUpdateSubmitted) {
        updateBlueprint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun BlueprintUpdateHandler.updateBlueprint(req: BlueprintUpdateSubmitted): Blueprint {
    return blueprintRepository.update(
        req.blueprintId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
            value = req.value
        )
    )
}

private fun BlueprintUpdateHandler.emitEvent(cmdId: CmdId, bp: Blueprint) {
    eventEmitter.emit(cmdId, BlueprintUpdatedEvent(bp))
}