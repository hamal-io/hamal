package io.hamal.repository.record.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.Serializable
import io.hamal.repository.record.Record
import kotlinx.serialization.SerialName

@Serializable
sealed class ExtensionRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<ExtensionId>()

@Serializable
@SerialName("ECR")
data class ExtensionCreationRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: ExtensionName,
    val code: ExtensionCode

) : ExtensionRecord()

@Serializable
@SerialName("EUR")
data class ExtensionUpdatedRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val name: ExtensionName,
    val code: ExtensionCode
) : ExtensionRecord()
