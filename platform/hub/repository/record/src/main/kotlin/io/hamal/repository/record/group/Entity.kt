package io.hamal.repository.record.group

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.repository.api.Group
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class GroupEntity(
    override val id: GroupId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var name: GroupName?,
    var creatorId: AccountId?

) : RecordEntity<GroupId, GroupRecord, Group> {

    override fun apply(rec: GroupRecord): GroupEntity {
        return when (rec) {
            is GroupCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                creatorId = rec.creatorId
            )
        }
    }

    override fun toDomainObject(): Group {
        return Group(
            cmdId = cmdId,
            id = id,
            name = name!!,
            creatorId = creatorId!!
        )
    }
}

fun List<GroupRecord>.createEntity(): GroupEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is GroupCreationRecord)

    var result = GroupEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        name = firstRecord.name,
        creatorId = firstRecord.creatorId
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateGroupFromRecords : CreateDomainObject<GroupId, GroupRecord, Group> {
    override fun invoke(recs: List<GroupRecord>): Group {
        return recs.createEntity().toDomainObject()
    }
}