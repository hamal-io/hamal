package io.hamal.backend.repository.api.record.exec

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets
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
    override val entityId: ExecId,
    override val cmdId: CmdId,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val secrets: ExecSecrets,
    val code: Code,
) : ExecRecord()

@Serializable
@SerialName("ESCR")
data class ExecScheduledRecord(
    override val entityId: ExecId,
    override val cmdId: CmdId,
) : ExecRecord()

@Serializable
@SerialName("EQR")
data class ExecQueuedRecord(
    override val entityId: ExecId,
    override val cmdId: CmdId,
) : ExecRecord()


@Serializable
@SerialName("ESTR")
data class ExecStartedRecord(
    override val entityId: ExecId,
    override val cmdId: CmdId,
) : ExecRecord()

@Serializable
@SerialName("ECR")
data class ExecCompletedRecord(
    override val entityId: ExecId,
    override val cmdId: CmdId,
) : ExecRecord()