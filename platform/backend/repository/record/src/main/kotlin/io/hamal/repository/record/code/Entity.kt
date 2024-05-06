package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Code
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt


data class CodeEntity(
    override val cmdId: CmdId,
    override val id: CodeId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    val workspaceId: WorkspaceId,
    var value: ValueCode? = null,
    val type: CodeType? = null

) : RecordEntity<CodeId, CodeRecord, Code> {

    override fun apply(rec: CodeRecord): CodeEntity {
        return when (rec) {
            is CodeRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                value = rec.value,
                type = rec.type,
                recordedAt = rec.recordedAt()

            )

            is CodeRecord.Updated -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                value = rec.value,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Code {
        return Code(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            workspaceId = workspaceId,
            version = CodeVersion(sequence.value),
            value = value!!,
            type = type!!
        )
    }
}

fun List<CodeRecord>.createEntity(): CodeEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is CodeRecord.Created)

    var result = CodeEntity(
        id = firstRecord.entityId,
        workspaceId = firstRecord.workspaceId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateCodeFromRecords : CreateDomainObject<CodeId, CodeRecord, Code> {
    override fun invoke(recs: List<CodeRecord>): Code {
        return recs.createEntity().toDomainObject()
    }
}