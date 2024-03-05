package io.hamal.repository.memory.record.hook

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.HookRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.hook.CreateHookFromRecords
import io.hamal.repository.record.hook.HookRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class HookMemoryRepository : RecordMemoryRepository<HookId, HookRecord, Hook>(
    createDomainObject = CreateHookFromRecords,
    recordClass = HookRecord::class,
    projections = listOf(ProjectionCurrent())
), HookRepository {
    override fun create(cmd: HookCmdRepository.CreateCmd): Hook {
        return lock.withLock {
            val hookId = cmd.hookId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, hookId)) {
                versionOf(hookId, cmd.id)
            } else {
                store(
                    HookRecord.Created(
                        cmdId = cmd.id,
                        entityId = hookId,
                        workspaceId = cmd.workspaceId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name
                    )
                )
                (currentVersion(hookId)).also(currentProjection::upsert)
            }
        }
    }

    override fun update(hookId: HookId, cmd: HookCmdRepository.UpdateCmd): Hook {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, hookId)) {
                versionOf(hookId, cmd.id)
            } else {
                val currentVersion = versionOf(hookId, cmd.id)
                store(
                    HookRecord.Updated(
                        entityId = hookId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name
                    )
                )
                (currentVersion(hookId)).also(currentProjection::upsert)
            }
        }
    }

    override fun find(hookId: HookId): Hook? = lock.withLock { currentProjection.find(hookId) }

    override fun list(query: HookQuery): List<Hook> = lock.withLock { currentProjection.list(query) }

    override fun count(query: HookQuery): Count = lock.withLock { currentProjection.count(query) }

    override fun close() {}

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}
