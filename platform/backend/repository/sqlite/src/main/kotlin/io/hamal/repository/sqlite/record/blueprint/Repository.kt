package io.hamal.repository.sqlite.record.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.CreateCmd
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.blueprint.BlueprintCreationRecord
import io.hamal.repository.record.blueprint.BlueprintEntity
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.blueprint.BlueprintUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateBlueprint : CreateDomainObject<BlueprintId, BlueprintRecord, Blueprint> {
    override fun invoke(recs: List<BlueprintRecord>): Blueprint {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is BlueprintCreationRecord)

        var result = BlueprintEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            creatorId = firstRecord.creatorId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteBlueprintRepository(
    config: Config
) : SqliteRecordRepository<BlueprintId, BlueprintRecord, Blueprint>(
    config = config,
    createDomainObject = CreateBlueprint,
    recordClass = BlueprintRecord::class,
    projections = listOf(ProjectionCurrent)
), BlueprintRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "blueprint.db"
    }

    override fun create(cmd: CreateCmd): Blueprint {
        val bpId = cmd.blueprintId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, bpId)) {
                versionOf(bpId, cmdId)
            } else {
                store(
                    BlueprintCreationRecord(
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
                    BlueprintUpdatedRecord(
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

    override fun count(query: BlueprintQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}