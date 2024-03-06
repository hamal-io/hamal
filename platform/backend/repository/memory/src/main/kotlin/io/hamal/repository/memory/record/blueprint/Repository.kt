package io.hamal.repository.memory.record.blueprint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository
import io.hamal.repository.api.BlueprintQueryRepository
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.blueprint.CreateBlueprintFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BlueprintMemoryRepository : RecordMemoryRepository<BlueprintId, BlueprintRecord, Blueprint>(
    createDomainObject = CreateBlueprintFromRecords,
    recordClass = BlueprintRecord::class,
    projections = listOf(ProjectionCurrent())
), BlueprintRepository {

    override fun create(cmd: BlueprintCmdRepository.CreateCmd): Blueprint {
        return lock.withLock {
            val bpId = cmd.blueprintId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, bpId)) {
                versionOf(bpId, cmd.id)
            } else {
                store(
                    BlueprintRecord.Created(
                        cmdId = cmd.id,
                        entityId = bpId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId,
                        description = cmd.description
                    )
                )
                (currentVersion(bpId)).also(currentProjection::upsert)
            }
        }
    }

    override fun update(blueprintId: BlueprintId, cmd: BlueprintCmdRepository.UpdateCmd): Blueprint {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, blueprintId)) {
                versionOf(blueprintId, cmd.id)
            } else {
                val currentVersion = versionOf(blueprintId, cmd.id)
                store(
                    BlueprintRecord.Updated(
                        entityId = blueprintId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value,
                        description = cmd.description ?: currentVersion.description
                    )
                )
                (currentVersion(blueprintId)).also(currentProjection::upsert)
            }
        }
    }

    override fun find(blueprintId: BlueprintId): Blueprint? = lock.withLock {
        currentProjection.find(blueprintId)
    }

    override fun list(query: BlueprintQueryRepository.BlueprintQuery): List<Blueprint> = lock.withLock {
        currentProjection.list(query)
    }

    override fun count(query: BlueprintQueryRepository.BlueprintQuery): Count = lock.withLock {
        currentProjection.count(query)
    }

    override fun close() {}

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()

}