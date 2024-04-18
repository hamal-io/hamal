package io.hamal.core.request.handler.recipe

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.RecipeCreateRequested
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeCmdRepository
import io.hamal.repository.api.event.RecipeCreatedEvent
import org.springframework.stereotype.Component

@Component
class RecipeCreateHandler(
    private val recipeCmdRepository: RecipeCmdRepository,
    private val eventEmitter: InternalEventEmitter,
) : RequestHandler<RecipeCreateRequested>(RecipeCreateRequested::class) {
    override fun invoke(req: RecipeCreateRequested) {
        createRecipe(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createRecipe(req: RecipeCreateRequested): Recipe {
        return recipeCmdRepository.create(
            RecipeCmdRepository.CreateCmd(
                id = req.cmdId(),
                recipeId = req.id,
                name = req.name,
                inputs = req.inputs,
                value = req.value,
                creatorId = req.creatorId,
                description = req.description ?: RecipeDescription.empty
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, recipe: Recipe) {
        eventEmitter.emit(cmdId, RecipeCreatedEvent(recipe))
    }
}

