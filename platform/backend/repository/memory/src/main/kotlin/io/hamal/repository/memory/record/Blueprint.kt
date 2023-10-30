package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.CreateCmd
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.record.blueprint.BlueprintCreationRecord
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.blueprint.BlueprintUpdatedRecord
import io.hamal.repository.record.blueprint.CreateBlueprintFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentBlueprintProjection {
    private val projection = mutableMapOf<BlueprintId, Blueprint>()
    fun apply(blueprint: Blueprint) {
        projection[blueprint.id] = blueprint
    }

    fun find(blueprintId: BlueprintId): Blueprint? = projection[blueprintId]

    fun list(query: BlueprintQuery): List<Blueprint> {
        return projection.filter { query.blueprintIds.isEmpty() || it.key in query.blueprintIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: BlueprintQuery): ULong {
        return projection.filter { query.blueprintIds.isEmpty() || it.key in query.blueprintIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryBlueprintRepository : MemoryRecordRepository<BlueprintId, BlueprintRecord, Blueprint>(
    createDomainObject = CreateBlueprintFromRecords,
    recordClass = BlueprintRecord::class
), BlueprintRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateCmd): Blueprint {
        return lock.withLock {
            val bpId = cmd.blueprintId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, bpId)) {
                versionOf(bpId, cmd.id)
            } else {
                store(
                    BlueprintCreationRecord(
                        cmdId = cmd.id,
                        entityId = bpId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId
                    )
                )
                (currentVersion(bpId)).also(CurrentBlueprintProjection::apply)
            }
        }
    }

    override fun update(blueprintId: BlueprintId, cmd: UpdateCmd): Blueprint {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, blueprintId)) {
                versionOf(blueprintId, cmd.id)
            } else {
                val currentVersion = versionOf(blueprintId, cmd.id)
                store(
                    BlueprintUpdatedRecord(
                        entityId = blueprintId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value
                    )
                )
                (currentVersion(blueprintId)).also(CurrentBlueprintProjection::apply)
            }
        }
    }

    override fun find(blueprintId: BlueprintId): Blueprint? = CurrentBlueprintProjection.find(blueprintId)

    override fun list(query: BlueprintQuery): List<Blueprint> = CurrentBlueprintProjection.list(query)

    override fun count(query: BlueprintQuery): ULong = CurrentBlueprintProjection.count(query)

    override fun clear() {
        super.clear()
        CurrentBlueprintProjection.clear()
    }

    override fun close() {}
}