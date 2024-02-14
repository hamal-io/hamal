package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintCmdRepository.CreateCmd
import io.hamal.repository.api.BlueprintCmdRepository.UpdateCmd
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.BlueprintRepository
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.blueprint.CreateBlueprintFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object BlueprintCurrentProjection {
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
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: BlueprintQuery): Count {
        return Count(
            projection.filter { query.blueprintIds.isEmpty() || it.key in query.blueprintIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

class BlueprintMemoryRepository : RecordMemoryRepository<BlueprintId, BlueprintRecord, Blueprint>(
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
                (currentVersion(bpId)).also(BlueprintCurrentProjection::apply)
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
                    BlueprintRecord.Updated(
                        entityId = blueprintId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value,
                        description = cmd.description ?: currentVersion.description
                    )
                )
                (currentVersion(blueprintId)).also(BlueprintCurrentProjection::apply)
            }
        }
    }

    override fun find(blueprintId: BlueprintId): Blueprint? =
        lock.withLock { BlueprintCurrentProjection.find(blueprintId) }

    override fun list(query: BlueprintQuery): List<Blueprint> = lock.withLock { BlueprintCurrentProjection.list(query) }

    override fun count(query: BlueprintQuery): Count = lock.withLock { BlueprintCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            BlueprintCurrentProjection.clear()
        }
    }

    override fun close() {}
}