package io.hamal.repository.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ExecRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<ExecId>()

@Serializable
@SerialName("ExecPlannedRecord")
data class ExecPlannedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val flowId: FlowId,
    val groupId: GroupId,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: ExecCode,
    val events: List<Event>
) : ExecRecord()

@Serializable
@SerialName("ExecScheduledRecord")
data class ExecScheduledRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

@Serializable
@SerialName("ExecQueuedRecord")
data class ExecQueuedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()


@Serializable
@SerialName("ExecStartedRecord")
data class ExecStartedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

@Serializable
@SerialName("ExecCompletedRecord")
data class ExecCompletedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult,
    val state: ExecState
) : ExecRecord()

@Serializable
@SerialName("ExecFailedRecord")
data class ExecFailedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult
) : ExecRecord()