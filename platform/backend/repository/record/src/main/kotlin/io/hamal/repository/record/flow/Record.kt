package io.hamal.repository.record.flow

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence

sealed class FlowRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FlowId>()

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