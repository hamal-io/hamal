package io.hamal.repository.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.Transient

sealed class ExecRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<ExecId>()

data class ExecPlannedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val flowId: FlowId,
    val groupId: GroupId,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: ExecCode,
    val invocation: Invocation
) : ExecRecord()

data class ExecScheduledRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

data class ExecQueuedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()


data class ExecStartedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
) : ExecRecord()

data class ExecCompletedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult,
    val state: ExecState
) : ExecRecord()

data class ExecFailedRecord(
    override val cmdId: CmdId,
    override val entityId: ExecId,
    val result: ExecResult
) : ExecRecord()