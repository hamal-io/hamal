package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.CodeId
import io.hamal.repository.api.CodeValue
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import io.hamal.repository.record.Record
import kotlinx.serialization.SerialName

@Serializable
sealed class CodeRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<CodeId>()

@Serializable
@SerialName("CCR")
data class CodeCreationRecord(
    override val entityId: CodeId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val code: CodeValue
) : CodeRecord()

@Serializable
@SerialName("CUR")
data class CodeUpdatedRecord(
    override val entityId: CodeId,
    override val cmdId: CmdId,
    val code: CodeValue
) : CodeRecord()