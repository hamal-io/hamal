package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeId
import io.hamal.repository.api.CodeValue
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence



class CodeEntity(
    override val cmdId: CmdId,
    override val id: CodeId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var code: CodeValue? = null

) : RecordEntity<CodeId, CodeRecord, Code> {

    override fun apply(rec: CodeRecord): CodeEntity {
        return when (rec) {
            is CodeCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                code = rec.code
            )

            is CodeUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                code = rec.code
            )
        }
    }

    override fun toDomainObject(): Code {
        TODO("Not yet implemented")
    }
}

fun List<CodeRecord>.createEntity(): CodeEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is CodeCreationRecord)

    var result = CodeEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
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