package io.hamal.repository.record.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.FlowId
import io.hamal.repository.api.Hook
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class HookEntity(
    override val cmdId: CmdId,
    override val id: HookId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var flowId: FlowId? = null,
    var name: HookName? = null

) : RecordEntity<HookId, HookRecord, Hook> {

    override fun apply(rec: HookRecord): HookEntity {
        return when (rec) {
            is HookCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                flowId = rec.flowId,
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
            flowId = flowId!!,
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
        sequence = firstRecord.sequence()
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