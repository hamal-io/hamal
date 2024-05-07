package io.hamal.repository.record.recipe

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class RecipeRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<RecipeId>() {

    internal object Adapter : RecordAdapter<RecipeRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: RecipeId,
        override val cmdId: CmdId,
        val creatorId: AccountId,
        val name: RecipeName,
        val inputs: RecipeInputs,
        val value: ValueCode,
        val description: RecipeDescription
    ) : RecipeRecord()


    data class Updated(
        override val entityId: RecipeId,
        override val cmdId: CmdId,
        val name: RecipeName,
        val inputs: RecipeInputs,
        val value: ValueCode,
        val description: RecipeDescription
    ) : RecipeRecord()
}

