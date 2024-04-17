package io.hamal.repository.record.recipe

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Recipe
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class RecipeEntity(
    override val cmdId: CmdId,
    override val id: RecipeId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    var creatorId: AccountId,

    var name: RecipeName? = null,
    var inputs: RecipeInputs? = null,
    var codeValue: CodeValue? = null,
    var description: RecipeDescription? = null,

    ) : RecordEntity<RecipeId, RecipeRecord, Recipe> {
    override fun apply(rec: RecipeRecord): RecipeEntity {
        return when (rec) {
            is RecipeRecord.Created -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                creatorId = rec.creatorId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value,
                description = rec.description,
                recordedAt = rec.recordedAt()
            )

            is RecipeRecord.Updated -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value,
                description = rec.description,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Recipe {
        return Recipe(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            creatorId = creatorId,
            name = name!!,
            inputs = inputs!!,
            value = codeValue!!,
            description = description!!
        )
    }
}

fun List<RecipeRecord>.createEntity(): RecipeEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is RecipeRecord.Created)

    var result = RecipeEntity(
        cmdId = firstRecord.cmdId,
        id = firstRecord.entityId,
        creatorId = firstRecord.creatorId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateRecipeFromRecords : CreateDomainObject<RecipeId, RecipeRecord, Recipe> {
    override fun invoke(recs: List<RecipeRecord>): Recipe {
        return recs.createEntity().toDomainObject()
    }
}