package io.hamal.repository.record.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Hook
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class HookEntity(
    override val cmdId: CmdId,
    override val id: HookId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val groupId: GroupId,


    var namespaceId: NamespaceId? = null,
    var name: HookName? = null

) : RecordEntity<HookId, HookRecord, Hook> {

    override fun apply(rec: HookRecord): HookEntity {
        return when (rec) {
            is HookCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                namespaceId = rec.namespaceId,
                name = rec.name
            )

            is HookUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name
            )
        }
    }

    override fun toDomainObject(): Hook {
        return Hook(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
            namespaceId = namespaceId!!,
            name = name!!
        )
    }
}

fun List<HookRecord>.createEntity(): HookEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is HookCreatedRecord)

    var result = HookEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = RecordedAt.now()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateHookFromRecords : CreateDomainObject<HookId, HookRecord, Hook> {
    override fun invoke(recs: List<HookRecord>): Hook {
        return recs.createEntity().toDomainObject()
    }
}