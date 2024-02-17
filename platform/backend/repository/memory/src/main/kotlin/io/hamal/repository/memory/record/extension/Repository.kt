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
                    ExtensionRecord.Created(
                        cmdId = cmd.id,
                        entityId = extId,
                        workspaceId = cmd.workspaceId,
                        name = cmd.name,
                        code = cmd.code
                    )
                )
                (currentVersion(extId)).also(ExtensionCurrentProjection::apply)
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
                    ExtensionRecord.Updated(
                        entityId = extId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                (currentVersion(extId)).also(ExtensionCurrentProjection::apply)
            }
        }
    }

    override fun close() {}

    override fun find(extId: ExtensionId): Extension? = lock.withLock { ExtensionCurrentProjection.find(extId) }

    override fun list(query: ExtensionQuery): List<Extension> = lock.withLock { ExtensionCurrentProjection.list(query) }

    override fun count(query: ExtensionQuery): Count = lock.withLock { ExtensionCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            ExtensionCurrentProjection.clear()
        }
    }
}