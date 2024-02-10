package io.hamal.repository.record.blueprint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class BlueprintRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<BlueprintId>() {

    internal object Adapter : RecordAdapter<BlueprintRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: BlueprintId,
        override val cmdId: CmdId,
        val groupId: GroupId,
        val creatorId: AccountId,
        val name: BlueprintName,
        val inputs: BlueprintInputs,
        val value: CodeValue
    ) : BlueprintRecord()


    data class Updated(
        override val entityId: BlueprintId,
        override val cmdId: CmdId,
        val name: BlueprintName,
        val inputs: BlueprintInputs,
        val value: CodeValue
    ) : BlueprintRecord()
}

