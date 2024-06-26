package io.hamal.repository.record.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class ExtensionEntity(
    override val cmdId: CmdId,
    override val id: ExtensionId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val workspaceId: WorkspaceId,

    var name: ExtensionName? = null,
    var code: ExtensionCode? = null
) : RecordEntity<ExtensionId, ExtensionRecord, Extension> {
    override fun apply(rec: ExtensionRecord): ExtensionEntity {
        return when (rec) {
            is ExtensionRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                code = rec.code,
                recordedAt = rec.recordedAt()
            )

            is ExtensionRecord.Updated -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                name = rec.name,
                code = rec.code,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Extension {
        return Extension(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            workspaceId = workspaceId,
            name = name!!,
            code = code!!
        )
    }
}

fun List<ExtensionRecord>.createEntity(): ExtensionEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is ExtensionRecord.Created)

    var result = ExtensionEntity(
        id = firstRecord.entityId,
        workspaceId = firstRecord.workspaceId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateExtensionFromRecords : CreateDomainObject<ExtensionId, ExtensionRecord, Extension> {
    override fun invoke(recs: List<ExtensionRecord>): Extension {
        return recs.createEntity().toDomainObject()
    }
}