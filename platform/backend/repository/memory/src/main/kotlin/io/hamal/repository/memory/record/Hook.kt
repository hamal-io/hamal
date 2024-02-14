package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.HookRepository
import io.hamal.repository.record.hook.CreateHookFromRecords
import io.hamal.repository.record.hook.HookRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object HookCurrentProjection {
    private val projection = mutableMapOf<HookId, Hook>()
    fun apply(hook: Hook) {
        val currentHook = projection[hook.id]
        projection.remove(hook.id)

        val hooksInNamespace = projection.values.filter { it.namespaceId == hook.namespaceId }
        if (hooksInNamespace.any { it.name == hook.name }) {
            if (currentHook != null) {
                projection[currentHook.id] = currentHook
            }
            throw IllegalArgumentException("${hook.name} already exists in namespace ${hook.namespaceId}")
        }

        projection[hook.id] = hook
    }

    fun find(hookId: HookId): Hook? = projection[hookId]

    fun list(query: HookQuery): List<Hook> {
        return projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: HookQuery): Count {
        return Count(
            projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

class HookMemoryRepository : RecordMemoryRepository<HookId, HookRecord, Hook>(
    createDomainObject = CreateHookFromRecords,
    recordClass = HookRecord::class
), HookRepository {
    private val lock = ReentrantLock()
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
                (currentVersion(hookId)).also(HookCurrentProjection::apply)
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
                (currentVersion(hookId)).also(HookCurrentProjection::apply)
            }
        }
    }

    override fun find(hookId: HookId): Hook? = lock.withLock { HookCurrentProjection.find(hookId) }

    override fun list(query: HookQuery): List<Hook> = lock.withLock { HookCurrentProjection.list(query) }

    override fun count(query: HookQuery): Count = lock.withLock { HookCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            HookCurrentProjection.clear()
        }
    }

    override fun close() {}
}
