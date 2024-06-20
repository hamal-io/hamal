package io.hamal.repository.record.namespace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.WorkspaceId
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
    val workspaceId: WorkspaceId,
    var name: NamespaceName? = null,
    var features: NamespaceFeatures? = null

) : RecordEntity<NamespaceId, NamespaceRecord, Namespace> {

    override fun apply(rec: NamespaceRecord): NamespaceEntity {
        return when (rec) {
            is NamespaceRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                features = rec.features,
                recordedAt = rec.recordedAt()
            )

            is NamespaceRecord.Updated -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                features = rec.features,
                recordedAt = rec.recordedAt()
            )

            is NamespaceRecord.Deleted -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Namespace {
        return Namespace(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            workspaceId = workspaceId,
            name = name!!,
            features = features!!
        )
    }
}

fun List<NamespaceRecord>.createEntity(): NamespaceEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is NamespaceRecord.Created)

    var result = NamespaceEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt(),
        workspaceId = firstRecord.workspaceId
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