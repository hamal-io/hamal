package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class NamespaceRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<NamespaceId>() {
    internal object Adapter : RecordAdapter<NamespaceRecord>(
        listOf(
            NamespaceCreatedRecord::class,
            NamespaceUpdatedRecord::class
        )
    )
}

data class NamespaceCreatedRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
    val type: NamespaceType,
) : NamespaceRecord()

data class NamespaceUpdatedRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
) : NamespaceRecord()