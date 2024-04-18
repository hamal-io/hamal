package io.hamal.core.request.handler.recipe

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.RecipeUpdateRequested
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeCmdRepository.UpdateCmd
import io.hamal.repository.api.RecipeRepository
import io.hamal.repository.api.event.RecipeUpdatedEvent
import org.springframework.stereotype.Component

@Component
class RecipeUpdateHandler(
    private val recipeRepository: RecipeRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<RecipeUpdateRequested>(RecipeUpdateRequested::class) {

    override fun invoke(req: RecipeUpdateRequested) {
        updateRecipe(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateRecipe(req: RecipeUpdateRequested): Recipe {
        return recipeRepository.update(
            req.id, UpdateCmd(
                id = req.cmdId(),
                name = req.name,
                inputs = req.inputs,
                value = req.value,
                description = req.description
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, bp: Recipe) {
        eventEmitter.emit(cmdId, RecipeUpdatedEvent(bp))
    }
}

