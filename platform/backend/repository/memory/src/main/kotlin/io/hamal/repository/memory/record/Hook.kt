package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.HookRepository
import io.hamal.repository.record.hook.CreateHookFromRecords
import io.hamal.repository.record.hook.HookCreatedRecord
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.record.hook.HookUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentHookProjection {
    private val projection = mutableMapOf<HookId, Hook>()
    fun apply(hook: Hook) {
        val currentHook = projection[hook.id]
        projection.remove(hook.id)

        val hooksInFlow = projection.values.filter { it.flowId == hook.flowId }
        if (hooksInFlow.any { it.name == hook.name }) {
            if (currentHook != null) {
                projection[currentHook.id] = currentHook
            }
            throw IllegalArgumentException("${hook.name} already exists in flow ${hook.flowId}")
        }

        projection[hook.id] = hook
    }

    fun find(hookId: HookId): Hook? = projection[hookId]

    fun list(query: HookQuery): List<Hook> {
        return projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: HookQuery): ULong {
        return projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryHookRepository : MemoryRecordRepository<HookId, HookRecord, Hook>(
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
                    HookCreatedRecord(
                        cmdId = cmd.id,
                        entityId = hookId,
                        groupId = cmd.groupId,
                        flowId = cmd.flowId,
                        name = cmd.name
                    )
                )
                (currentVersion(hookId)).also(CurrentHookProjection::apply)
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
                    HookUpdatedRecord(
                        entityId = hookId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name
                    )
                )
                (currentVersion(hookId)).also(CurrentHookProjection::apply)
            }
        }
    }

    override fun find(hookId: HookId): Hook? = lock.withLock { CurrentHookProjection.find(hookId) }

    override fun list(query: HookQuery): List<Hook> = lock.withLock { CurrentHookProjection.list(query) }

    override fun count(query: HookQuery): ULong = lock.withLock { CurrentHookProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            CurrentHookProjection.clear()
        }
    }

    override fun close() {}
}
