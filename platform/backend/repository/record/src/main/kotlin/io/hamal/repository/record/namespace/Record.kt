package io.hamal.repository.record.flow

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed class FlowRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<FlowId>()

@Serializable
@SerialName("FlowCreatedRecord")
data class FlowCreatedRecord(
    override val entityId: FlowId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: FlowName,
    val inputs: FlowInputs,
) : FlowRecord()

@Serializable
@SerialName("FlowUpdatedRecord")
data class FlowUpdatedRecord(
    override val entityId: FlowId,
    override val cmdId: CmdId,
    val name: FlowName,
    val inputs: FlowInputs,
) : FlowRecord()