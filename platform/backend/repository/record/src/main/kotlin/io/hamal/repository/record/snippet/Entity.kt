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
    var accountId: AccountId,
    override val sequence: RecordSequence,

    var name: SnippetName? = null,
    var inputs: SnippetInputs? = null,
    var codeValue: CodeValue? = null

) : RecordEntity<SnippetId, SnippetRecord, Snippet> {
    override fun apply(rec: SnippetRecord): SnippetEntity {
        return when (rec) {
            is SnippetCreationRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                accountId = rec.accountId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value
            )

            is SnippetUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value
            )
        }
    }

    override fun toDomainObject(): Snippet {
        return Snippet(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
            accountId = accountId,
            name = name!!,
            inputs = inputs!!,
            value = codeValue!!
        )
    }
}

fun List<SnippetRecord>.createEntity(): SnippetEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is SnippetCreationRecord)

    var result = SnippetEntity(
        cmdId = firstRecord.cmdId,
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        accountId = firstRecord.accountId,
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