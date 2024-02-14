package io.hamal.repository.sqlite.record.extension

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.extension.ExtensionEntity
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateExtension : CreateDomainObject<ExtensionId, ExtensionRecord, Extension> {
    override fun invoke(recs: List<ExtensionRecord>): Extension {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is ExtensionRecord.Created)

        var result = ExtensionEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            workspaceId = firstRecord.workspaceId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class ExtensionSqliteRepository(
    path: Path
) : RecordSqliteRepository<ExtensionId, ExtensionRecord, Extension>(
    path = path,
    filename = "extension.db",
    createDomainObject = CreateExtension,
    recordClass = ExtensionRecord::class,
    projections = listOf(
        ProjectionCurrent
    )
), ExtensionRepository {

    override fun create(cmd: CreateCmd): Extension {
        val extId = cmd.extId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, extId)) {
                versionOf(extId, cmdId)
            } else {
                store(
                    ExtensionRecord.Created(
                        cmdId = cmdId,
                        entityId = extId,
                        workspaceId = cmd.workspaceId,
                        name = cmd.name,
                        code = cmd.code
                    )
                )
                currentVersion(extId).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun update(extId: ExtensionId, cmd: UpdateCmd): Extension {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, extId)) {
                versionOf(extId, cmdId)
            } else {
                val currentVersion = versionOf(extId, cmdId)
                store(
                    ExtensionRecord.Updated(
                        entityId = extId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                currentVersion(extId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(extId: ExtensionId): Extension? = ProjectionCurrent.find(connection, extId)


    override fun list(query: ExtensionQuery): List<Extension> = ProjectionCurrent.list(connection, query)


    override fun count(query: ExtensionQuery): Count = ProjectionCurrent.count(connection, query)

}