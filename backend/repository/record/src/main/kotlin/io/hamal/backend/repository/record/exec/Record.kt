package io.hamal.backend.repository.api.record.exec

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.record.RecordType
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets

interface ExecRecord : Record<ExecId, ExecRecord.Type> {
    enum class Type(override val value: Int) : RecordType {
        Planned(1),
        Scheduled(2),
        Enqueued(3)
    }
}

data class ExecPlannedRecord(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val prevCmdId: CmdId,
    override val sequence: RecordSequence,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val secrets: ExecSecrets,
    val code: Code,
) : ExecRecord {
    override val type = ExecRecord.Type.Planned
}

data class ExecScheduledRecord(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val prevCmdId: CmdId,
    override val sequence: RecordSequence,
) : ExecRecord {
    override val type = ExecRecord.Type.Scheduled
}


data class ExecQueuedRecord(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val prevCmdId: CmdId,
    override val sequence: RecordSequence,
) : ExecRecord {
    override val type = ExecRecord.Type.Enqueued
}


data class ExecStartedRecord(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val prevCmdId: CmdId,
    override val sequence: RecordSequence,
) : ExecRecord {
    override val type = ExecRecord.Type.Enqueued
}


data class ExecCompletedRecord(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val prevCmdId: CmdId,
    override val sequence: RecordSequence,
) : ExecRecord {
    override val type = ExecRecord.Type.Enqueued
}
