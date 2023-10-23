package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.record.extension.CreateExtensionFromRecords
import io.hamal.repository.record.extension.ExtensionCreationRecord
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.record.extension.ExtensionUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentExtensionProjection {
    private val projection = mutableMapOf<ExtensionId, Extension>()
    fun apply(ext: Extension) {
        val currentExt = projection[ext.id]
        projection.remove(ext.id)

        val extInGroup = projection.values.filter { it.groupId == ext.groupId }
        if (extInGroup.any { it.name == ext.name }) {
            if (currentExt != null) {
                projection[currentExt.id] = currentExt
            }
            throw IllegalArgumentException("${ext.name} already exists in group ${ext.groupId}")
        }

        projection[ext.id] = ext
    }

    fun find(extId: ExtensionId): Extension? = projection[extId]

    fun list(query: ExtensionQuery): List<Extension> {
        return projection.filter { query.extIds.isEmpty() || it.key in query.extIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: ExtensionQuery): ULong {
        return projection.filter { query.extIds.isEmpty() || it.key in query.extIds }
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

class MemoryExtensionRepository : MemoryRecordRepository<ExtensionId, ExtensionRecord, Extension>(
    createDomainObject = CreateExtensionFromRecords,
    recordClass = ExtensionRecord::class
), ExtensionRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: CreateCmd): Extension {
        return lock.withLock {
            val extId = cmd.extId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, extId)) {
                versionOf(extId, cmd.id)
            } else {
                store(
                    ExtensionCreationRecord(
                        cmdId = cmd.id,
                        entityId = extId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        code = cmd.code
                    )
                )
                (currentVersion(extId)).also(CurrentExtensionProjection::apply)
            }
        }
    }

    override fun update(extId: ExtensionId, cmd: UpdateCmd): Extension {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, extId)) {
                versionOf(extId, cmd.id)
            } else {
                val currentVersion = versionOf(extId, cmd.id)
                store(
                    ExtensionUpdatedRecord(
                        entityId = extId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                (currentVersion(extId)).also(CurrentExtensionProjection::apply)
            }
        }
    }

    override fun close() {}

    override fun find(extId: ExtensionId): Extension? = CurrentExtensionProjection.find(extId)

    override fun list(query: ExtensionQuery): List<Extension> = CurrentExtensionProjection.list(query)

    override fun count(query: ExtensionQuery): ULong = CurrentExtensionProjection.count(query)

    override fun clear() {
        super.clear()
        CurrentExtensionProjection.clear()
    }
}