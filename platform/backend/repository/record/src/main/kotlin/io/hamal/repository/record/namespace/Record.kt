package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class FlowRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FlowId>() {
    internal object Adapter : RecordAdapter<FlowRecord>(
        listOf(
            FlowCreatedRecord::class,
            FlowUpdatedRecord::class
        )
    )
}

data class FlowCreatedRecord(
    override val entityId: FlowId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: FlowName,
    val inputs: FlowInputs,
    val type: FlowType,
) : FlowRecord()

data class FlowUpdatedRecord(
    override val entityId: FlowId,
    override val cmdId: CmdId,
    val name: FlowName,
    val inputs: FlowInputs,
) : FlowRecord()