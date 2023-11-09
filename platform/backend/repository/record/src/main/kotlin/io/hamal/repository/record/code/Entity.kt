package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Code
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence


data class CodeEntity(
    override val cmdId: CmdId,
    override val id: CodeId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    val groupId: GroupId,
    var value: CodeValue? = null,
    val type: CodeType? = null

) : RecordEntity<CodeId, CodeRecord, Code> {

    override fun apply(rec: CodeRecord): CodeEntity {
        return when (rec) {
            is CodeCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                value = rec.value,
                type = rec.type,
                recordedAt = rec.recordedAt()

            )

            is CodeUpdatedRecord -> copy(
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
            groupId = groupId,
            version = CodeVersion(sequence.value),
            value = value!!,
            type = type!!,

        )
    }
}

fun List<CodeRecord>.createEntity(): CodeEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is CodeCreatedRecord)


    var result = CodeEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
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