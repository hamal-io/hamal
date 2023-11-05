package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class NamespaceEntity(
    override val cmdId: CmdId,
    override val id: NamespaceId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var name: NamespaceName? = null,
    var inputs: NamespaceInputs? = null,

    ) : RecordEntity<NamespaceId, NamespaceRecord, Namespace> {

    override fun apply(rec: NamespaceRecord): NamespaceEntity {
        return when (rec) {
            is NamespaceCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
            )

            is NamespaceUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
            )
        }
    }

    override fun toDomainObject(): Namespace {
        return Namespace(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
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
        sequence = firstRecord.sequence()
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