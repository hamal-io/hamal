package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class CodeRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override val recordedAt: RecordedAt? = null
) : Record<CodeId>()

@Serializable
@SerialName("CodeCreatedRecord")
data class CodeCreatedRecord(
    override val entityId: CodeId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val value: CodeValue,
    val type: CodeType
) : CodeRecord()

@Serializable
@SerialName("CodeUpdatedRecord")
data class CodeUpdatedRecord(
    override val entityId: CodeId,
    override val cmdId: CmdId,
    val value: CodeValue
) : CodeRecord()