package io.hamal.repository.record.namespace

import io.hamal.repository.api.Namespace
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName

data class Entity(
    override val id: NamespaceId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var name: NamespaceName? = null,
    var inputs: NamespaceInputs? = null,

    ) : RecordEntity<NamespaceId, NamespaceRecord, Namespace> {

    override fun apply(rec: NamespaceRecord): Entity {
        return when (rec) {
            is NamespaceCreationRecord -> copy(
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
            name = name!!,
            inputs = inputs!!,
        )
    }
}

fun List<NamespaceRecord>.createEntity(): Entity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is NamespaceCreationRecord)

    var result = Entity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}