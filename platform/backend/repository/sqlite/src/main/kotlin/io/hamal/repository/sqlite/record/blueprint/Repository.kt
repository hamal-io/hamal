package io.hamal.repository.sqlite.record.blueprint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.CreateCmd
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.blueprint.BlueprintEntity
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateBlueprint : CreateDomainObject<BlueprintId, BlueprintRecord, Blueprint> {
    override fun invoke(recs: List<BlueprintRecord>): Blueprint {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is BlueprintRecord.Created)

        var result = BlueprintEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            creatorId = firstRecord.creatorId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class BlueprintSqliteRepository(
    path: Path
) : RecordSqliteRepository<BlueprintId, BlueprintRecord, Blueprint>(
    path = path,
    filename = "blueprint.db",
    createDomainObject = CreateBlueprint,
    recordClass = BlueprintRecord::class,
    projections = listOf(ProjectionCurrent)
), BlueprintRepository {

    override fun create(cmd: CreateCmd): Blueprint {
        val bpId = cmd.blueprintId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, bpId)) {
                versionOf(bpId, cmdId)
            } else {
                store(
                    BlueprintRecord.Created(
                        cmdId = cmdId,
                        entityId = bpId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId
                    )
                )

                currentVersion(bpId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun update(blueprintId: BlueprintId, cmd: UpdateCmd): Blueprint {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, blueprintId)) {
                versionOf(blueprintId, cmdId)
            } else {
                val currentVersion = versionOf(blueprintId, cmdId)
                store(
                    BlueprintRecord.Updated(
                        entityId = blueprintId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value
                    )
                )
                currentVersion(blueprintId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(blueprintId: BlueprintId): Blueprint? {
        return ProjectionCurrent.find(connection, blueprintId)

    }

    override fun list(query: BlueprintQuery): List<Blueprint> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: BlueprintQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}