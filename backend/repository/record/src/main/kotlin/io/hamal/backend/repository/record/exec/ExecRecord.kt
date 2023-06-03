package io.hamal.backend.repository.api.record.exec

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.record.RecordType
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId

interface ExecRecord : Record<ExecId, ExecRecord.Type> {
    enum class Type(override val value: Int) : RecordType {
        Planned(1),
        Scheduled(2)
    }
}

data class ExecPlannedRecord(
    override val id: ExecId,
    override val commandId: CommandId,
    override val previousCommandId: CommandId,
    override val sequence: RecordSequence,
    val code: Code
) : ExecRecord {
    override val type = ExecRecord.Type.Planned
}

data class ExecScheduledRecord(
    override val id: ExecId,
    override val commandId: CommandId,
    override val previousCommandId: CommandId,
    override val sequence: RecordSequence,
) : ExecRecord {
    override val type = ExecRecord.Type.Scheduled
}
