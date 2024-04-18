package io.hamal.repository.api.event

import io.hamal.repository.api.Recipe

data class RecipeCreatedEvent(
    val recipe: Recipe
) : InternalEvent()


data class RecipeUpdatedEvent(
    val recipe: Recipe,
) : InternalEvent()

