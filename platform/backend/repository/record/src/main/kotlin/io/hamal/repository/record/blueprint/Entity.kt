package io.hamal.repository.record.blueprint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Blueprint
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class BlueprintEntity(
    override val cmdId: CmdId,
    override val id: BlueprintId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val groupId: GroupId,
    var creatorId: AccountId,

    var name: BlueprintName? = null,
    var inputs: BlueprintInputs? = null,
    var codeValue: CodeValue? = null

) : RecordEntity<BlueprintId, BlueprintRecord, Blueprint> {
    override fun apply(rec: BlueprintRecord): BlueprintEntity {
        return when (rec) {
            is BlueprintCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                creatorId = rec.creatorId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value,
                recordedAt = rec.recordedAt()
            )

            is BlueprintUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                codeValue = rec.value,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Blueprint {
        return Blueprint(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            groupId = groupId,
            creatorId = creatorId,
            name = name!!,
            inputs = inputs!!,
            value = codeValue!!
        )
    }
}

fun List<BlueprintRecord>.createEntity(): BlueprintEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is BlueprintCreatedRecord)

    var result = BlueprintEntity(
        cmdId = firstRecord.cmdId,
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        creatorId = firstRecord.creatorId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateBlueprintFromRecords : CreateDomainObject<BlueprintId, BlueprintRecord, Blueprint> {
    override fun invoke(recs: List<BlueprintRecord>): Blueprint {
        return recs.createEntity().toDomainObject()
    }
}