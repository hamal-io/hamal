package io.hamal.repository.record.blueprint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class BlueprintRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<BlueprintId>()

@Serializable
@SerialName("BlueprintCreatedRecord")
data class BlueprintCreatedRecord(
    override val entityId: BlueprintId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val creatorId: AccountId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
) : BlueprintRecord()


@Serializable
@SerialName("BlueprintUpdatedRecord")
data class BlueprintUpdatedRecord(
    override val entityId: BlueprintId,
    override val cmdId: CmdId,
    val name: BlueprintName,
    val inputs: BlueprintInputs,
    val value: CodeValue
) : BlueprintRecord()