package io.hamal.repository.record.group

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class GroupRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<GroupId>()

@Serializable
@SerialName("GCR")
data class GroupCreationRecord(
    override val entityId: GroupId,
    override val cmdId: CmdId,
    val name: GroupName,
    val creatorId: AccountId
) : GroupRecord()