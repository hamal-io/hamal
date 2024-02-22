package io.hamal.core.request.handler.blueprint

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.BlueprintCreateRequested
import io.hamal.lib.domain.vo.BlueprintDescription
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository
import io.hamal.repository.api.event.BlueprintCreatedEvent
import org.springframework.stereotype.Component

@Component
class BlueprintCreateHandler(
    private val blueprintCmdRepository: BlueprintCmdRepository,
    private val eventEmitter: InternalEventEmitter,
) : RequestHandler<BlueprintCreateRequested>(BlueprintCreateRequested::class) {
    override fun invoke(req: BlueprintCreateRequested) {
        createBlueprint(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createBlueprint(req: BlueprintCreateRequested): Blueprint {
        return blueprintCmdRepository.create(
            BlueprintCmdRepository.CreateCmd(
                id = req.cmdId(),
                blueprintId = req.blueprintId,
                name = req.name,
                inputs = req.inputs,
                value = req.value,
                creatorId = req.creatorId,
                description = req.description ?: BlueprintDescription.empty
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, blueprint: Blueprint) {
        eventEmitter.emit(cmdId, BlueprintCreatedEvent(blueprint))
    }
}

