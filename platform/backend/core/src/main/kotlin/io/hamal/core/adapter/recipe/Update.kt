package io.hamal.core.adapter.recipe

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeUpdateRequest
import io.hamal.lib.domain.request.RecipeUpdateRequested
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface RecipeUpdatePort {
    operator fun invoke(recipeId: RecipeId, req: RecipeUpdateRequest): RecipeUpdateRequested
}

@Component
class RecipeUpdateAdapter(
    private val recipeGet: RecipeGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : RecipeUpdatePort {
    override fun invoke(recipeId: RecipeId, req: RecipeUpdateRequest): RecipeUpdateRequested {
        ensureRecipeExists(recipeId)
        return RecipeUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = recipeId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            description = req.description

        ).also(requestEnqueue::invoke)
    }

    private fun ensureRecipeExists(recipeId: RecipeId) = recipeGet(recipeId)
}