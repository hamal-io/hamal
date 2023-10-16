package io.hamal.repository.record.snippet

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Snippet
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class SnippetEntity(
    override val cmdId: CmdId,
    override val id: SnippetId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var name: SnippetName? = null,
    var inputs: SnippetInputs? = null,
    var codeValue: CodeValue? = null,
    var accountId: AccountId? = null

) : RecordEntity<SnippetId, SnippetRecord, Snippet> {
    override fun apply(rec: SnippetRecord): SnippetEntity {
        return when (rec) {
            is SnippetCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.codeValue,
                accountId = rec.accountId
            )

            is SnippetUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.codeValue
            )
        }
    }

    override fun toDomainObject(): Snippet {
        return Snippet(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
            name = name!!,
            inputs = inputs!!,
            codeValue = codeValue!!,
            accountId = accountId!!
        )
    }
}

fun List<SnippetRecord>.createEntity(): SnippetEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is SnippetCreationRecord)

    var result = SnippetEntity(
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

object CreateSnippetFromRecords : CreateDomainObject<SnippetId, SnippetRecord, Snippet> {
    override fun invoke(recs: List<SnippetRecord>): Snippet {
        return recs.createEntity().toDomainObject()
    }
}