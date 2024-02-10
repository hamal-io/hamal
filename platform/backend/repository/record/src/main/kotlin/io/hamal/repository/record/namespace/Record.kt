package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
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
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: NamespaceId,
        override val cmdId: CmdId,
        val groupId: GroupId,
        val name: NamespaceName
    ) : NamespaceRecord()

    data class Updated(
        override val entityId: NamespaceId,
        override val cmdId: CmdId,
        val name: NamespaceName
    ) : NamespaceRecord()
}

