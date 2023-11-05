package io.hamal.repository.record.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ExtensionRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<ExtensionId>()

@Serializable
@SerialName("ExtensionCreatedRecord")
data class ExtensionCreatedRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: ExtensionName,
    val code: ExtensionCode

) : ExtensionRecord()

@Serializable
@SerialName("ExtensionUpdatedRecord")
data class ExtensionUpdatedRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val name: ExtensionName,
    val code: ExtensionCode
) : ExtensionRecord()
