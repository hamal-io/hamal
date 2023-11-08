package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed class NamespaceRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override val recordedAt: RecordedAt? = null
) : Record<NamespaceId>()

@Serializable
@SerialName("NamespaceCreatedRecord")
data class NamespaceCreatedRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
) : NamespaceRecord()

@Serializable
@SerialName("NamespaceUpdatedRecord")
data class NamespaceUpdatedRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
) : NamespaceRecord()