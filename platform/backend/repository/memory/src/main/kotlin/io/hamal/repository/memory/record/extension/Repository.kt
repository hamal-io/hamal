package io.hamal.repository.memory.record.extension

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionCmdRepository.CreateCmd
import io.hamal.repository.api.ExtensionCmdRepository.UpdateCmd
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.ExtensionRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.extension.CreateExtensionFromRecords
import io.hamal.repository.record.extension.ExtensionRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ExtensionMemoryRepository : RecordMemoryRepository<ExtensionId, ExtensionRecord, Extension>(
    createDomainObject = CreateExtensionFromRecords,
    recordClass = ExtensionRecord::class,
    projections = listOf(ProjectionCurrent())

), ExtensionRepository {
    override fun create(cmd: CreateCmd): Extension {
        return lock.withLock {
            val extensionId = cmd.extensionId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, extensionId)) {
                versionOf(extensionId, cmd.id)
            } else {
                store(
                    ExtensionRecord.Created(
                        cmdId = cmd.id,
                        entityId = extensionId,
                        workspaceId = cmd.workspaceId,
                        name = cmd.name,
                        code = cmd.code
                    )
                )
                (currentVersion(extensionId)).also(currentProjection::upsert)
            }
        }
    }

    override fun update(extensionId: ExtensionId, cmd: UpdateCmd): Extension {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, extensionId)) {
                versionOf(extensionId, cmd.id)
            } else {
                val currentVersion = versionOf(extensionId, cmd.id)
                store(
                    ExtensionRecord.Updated(
                        entityId = extensionId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                (currentVersion(extensionId)).also(currentProjection::upsert)
            }
        }
    }

    override fun close() {}

    override fun find(extensionId: ExtensionId): Extension? = lock.withLock { currentProjection.find(extensionId) }

    override fun list(query: ExtensionQuery): List<Extension> = lock.withLock { currentProjection.list(query) }

    override fun count(query: ExtensionQuery): Count = lock.withLock { currentProjection.count(query) }

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()

}