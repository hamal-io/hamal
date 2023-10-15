package io.hamal.repository.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
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
@SerialName("EPR")
data class ExecPlannedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val groupId: GroupId,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    
    val code: CodeValue?,
    val codeId: CodeId?,
    val codeVersion: CodeVersion?,
    val events: List<Event>
) : ExecRecord()

@Serializable
@SerialName("ESCR")
data class ExecScheduledRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

@Serializable
@SerialName("EQR")
data class ExecQueuedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()


@Serializable
@SerialName("ESTR")
data class ExecStartedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

@Serializable
@SerialName("ECR")
data class ExecCompletedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult
) : ExecRecord()

@Serializable
@SerialName("EFR")
data class ExecFailedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult
) : ExecRecord()