package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
    val value: CodeValue,
    val type: CodeType
) : CodeRecord()

@Serializable
@SerialName("CUR")
data class CodeUpdatedRecord(
    override val entityId: CodeId,
    override val cmdId: CmdId,
    val value: CodeValue
) : CodeRecord()