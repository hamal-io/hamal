package io.hamal.repository.sqlite.record.recipe

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeCmdRepository.CreateCmd
import io.hamal.repository.api.RecipeCmdRepository.UpdateCmd
import io.hamal.repository.api.RecipeQueryRepository.RecipeQuery
import io.hamal.repository.api.RecipeRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.recipe.RecipeEntity
import io.hamal.repository.record.recipe.RecipeRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateRecipe : CreateDomainObject<RecipeId, RecipeRecord, Recipe> {
    override fun invoke(recs: List<RecipeRecord>): Recipe {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is RecipeRecord.Created)

        var result = RecipeEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            creatorId = firstRecord.creatorId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class RecipeSqliteRepository(
    path: Path
) : RecordSqliteRepository<RecipeId, RecipeRecord, Recipe>(
    path = path,
    filename = "recipe.db",
    createDomainObject = CreateRecipe,
    recordClass = RecipeRecord::class,
    projections = listOf(ProjectionCurrent)
), RecipeRepository {

    override fun create(cmd: CreateCmd): Recipe {
        val recipeId = cmd.recipeId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, recipeId)) {
                versionOf(recipeId, cmdId)
            } else {
                store(
                    RecipeRecord.Created(
                        cmdId = cmdId,
                        entityId = recipeId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId,
                        description = cmd.description
                    )
                )

                currentVersion(recipeId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun update(recipeId: RecipeId, cmd: UpdateCmd): Recipe {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, recipeId)) {
                versionOf(recipeId, cmdId)
            } else {
                val currentVersion = versionOf(recipeId, cmdId)
                store(
                    RecipeRecord.Updated(
                        entityId = recipeId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value,
                        description = cmd.description ?: currentVersion.description
                    )
                )
                currentVersion(recipeId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(recipeId: RecipeId): Recipe? {
        return ProjectionCurrent.find(connection, recipeId)

    }

    override fun list(query: RecipeQuery): List<Recipe> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: RecipeQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}