package io.hamal.repository.memory.record.recipe

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeCmdRepository
import io.hamal.repository.api.RecipeQueryRepository
import io.hamal.repository.api.RecipeRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.recipe.CreateRecipeFromRecords
import io.hamal.repository.record.recipe.RecipeRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RecipeMemoryRepository : RecordMemoryRepository<RecipeId, RecipeRecord, Recipe>(
    createDomainObject = CreateRecipeFromRecords,
    recordClass = RecipeRecord::class,
    projections = listOf(ProjectionCurrent())
), RecipeRepository {

    override fun create(cmd: RecipeCmdRepository.CreateCmd): Recipe {
        return lock.withLock {
            val recipeId = cmd.recipeId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, recipeId)) {
                versionOf(recipeId, cmd.id)
            } else {
                store(
                    RecipeRecord.Created(
                        cmdId = cmd.id,
                        entityId = recipeId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId,
                        description = cmd.description
                    )
                )
                (currentVersion(recipeId)).also(currentProjection::upsert)
            }
        }
    }

    override fun update(recipeId: RecipeId, cmd: RecipeCmdRepository.UpdateCmd): Recipe {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, recipeId)) {
                versionOf(recipeId, cmd.id)
            } else {
                val currentVersion = versionOf(recipeId, cmd.id)
                store(
                    RecipeRecord.Updated(
                        entityId = recipeId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value,
                        description = cmd.description ?: currentVersion.description
                    )
                )
                (currentVersion(recipeId)).also(currentProjection::upsert)
            }
        }
    }

    override fun find(recipeId: RecipeId): Recipe? = lock.withLock {
        currentProjection.find(recipeId)
    }

    override fun list(query: RecipeQueryRepository.RecipeQuery): List<Recipe> = lock.withLock {
        currentProjection.list(query)
    }

    override fun count(query: RecipeQueryRepository.RecipeQuery): Count = lock.withLock {
        currentProjection.count(query)
    }

    override fun close() {}

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()

}