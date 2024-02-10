package io.hamal.repository.record.endpoint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class EndpointRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<EndpointId>() {
    internal object Adapter : RecordAdapter<EndpointRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: EndpointId,
        override val cmdId: CmdId,
        val groupId: GroupId,
        val namespaceId: NamespaceId,
        val funcId: FuncId,
        val name: EndpointName
    ) : EndpointRecord()

    data class Updated(
        override val entityId: EndpointId,
        override val cmdId: CmdId,
        val funcId: FuncId,
        val name: EndpointName
    ) : EndpointRecord()

}

