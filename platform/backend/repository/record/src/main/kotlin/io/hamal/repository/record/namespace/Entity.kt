package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Namespace
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class NamespaceEntity(
    override val cmdId: CmdId,
    override val id: NamespaceId,
    override val recordedAt: RecordedAt,
    override val sequence: RecordSequence,
    val groupId: GroupId,

    var name: NamespaceName? = null,
    var inputs: NamespaceInputs? = null,
    val type: NamespaceType,
) : RecordEntity<NamespaceId, NamespaceRecord, Namespace> {

    override fun apply(rec: NamespaceRecord): NamespaceEntity {
        return when (rec) {
            is NamespaceCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                type = rec.type,
                name = rec.name,
                inputs = rec.inputs,
                recordedAt = rec.recordedAt()
            )

            is NamespaceUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Namespace {
        return Namespace(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            groupId = groupId,
            type = type,
            name = name!!,
            inputs = inputs!!,
        )
    }
}

fun List<NamespaceRecord>.createEntity(): NamespaceEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is NamespaceCreatedRecord)

    var result = NamespaceEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        type = firstRecord.type,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateNamespaceFromRecords : CreateDomainObject<NamespaceId, NamespaceRecord, Namespace> {
    override fun invoke(recs: List<NamespaceRecord>): Namespace {
        return recs.createEntity().toDomainObject()
    }
}