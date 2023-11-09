package io.hamal.repository.record.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class HookRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<HookId>()

@Serializable
@SerialName("HookCreatedRecord")
data class HookCreatedRecord(
    override val entityId: HookId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: HookName
) : HookRecord()

@Serializable
@SerialName("HookUpdatedRecord")
data class HookUpdatedRecord(
    override val entityId: HookId,
    override val cmdId: CmdId,
    val name: HookName
) : HookRecord()