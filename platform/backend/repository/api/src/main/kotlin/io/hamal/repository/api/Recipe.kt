package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.*

data class Recipe(
    override val id: RecipeId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val creatorId: AccountId,
    val name: RecipeName,
    val inputs: RecipeInputs,
    val value: ValueCode,
    val description: RecipeDescription
) : DomainObject<RecipeId>, HasAccountId {
    override val accountId: AccountId get() = creatorId
}

interface RecipeRepository : RecipeCmdRepository, RecipeQueryRepository

interface RecipeCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Recipe
    fun update(recipeId: RecipeId, cmd: UpdateCmd): Recipe

    data class CreateCmd(
        val id: CmdId,
        val recipeId: RecipeId,
        val creatorId: AccountId,
        val inputs: RecipeInputs,
        val name: RecipeName,
        val value: ValueCode,
        val description: RecipeDescription
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: RecipeName? = null,
        val inputs: RecipeInputs? = null,
        val value: ValueCode? = null,
        val description: RecipeDescription? = null
    )
}

interface RecipeQueryRepository {
    fun get(recipeId: RecipeId) = find(recipeId) ?: throw NoSuchElementException("Recipe not found")
    fun find(recipeId: RecipeId): Recipe?
    fun list(query: RecipeQuery): List<Recipe>
    fun list(recipeIds: List<RecipeId>): List<Recipe> = list(
        RecipeQuery(
            limit = Limit.all,
            recipeIds = recipeIds
        )
    )

    fun count(query: RecipeQuery): Count

    data class RecipeQuery(
        var afterId: RecipeId = RecipeId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var recipeIds: List<RecipeId> = listOf(),
    )
}