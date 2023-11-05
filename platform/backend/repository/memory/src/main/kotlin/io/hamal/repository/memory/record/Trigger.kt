package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerCmdRepository.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.record.trigger.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentTriggerProjection {

    private val projection = mutableMapOf<TriggerId, Trigger>()

    fun apply(trigger: Trigger) {
        val currentTrigger = projection[trigger.id]
        projection.remove(trigger.id)

        val triggersInNamespace = projection.values.filter { it.namespaceId == trigger.namespaceId }
        if (triggersInNamespace.any { it.name == trigger.name }) {
            if (currentTrigger != null) {
                projection[currentTrigger.id] = currentTrigger
            }
            throw IllegalArgumentException("${trigger.name} already exists in namespace ${trigger.namespaceId}")
        }

        projection[trigger.id] = trigger
    }

    fun find(triggerId: TriggerId): Trigger? = projection[triggerId]

    fun list(query: TriggerQuery): List<Trigger> {
        return projection.filter { query.triggerIds.isEmpty() || it.key in query.triggerIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.funcIds.isEmpty()) true else query.funcIds.contains(it.funcId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .filter {
                if (query.topicIds.isEmpty()) {
                    true
                } else {
                    if (it is EventTrigger) {
                        query.topicIds.contains(it.topicId)
                    } else {
                        false
                    }
                }
            }
            .filter {
                if (query.hookIds.isEmpty()) {
                    true
                } else {
                    if (it is HookTrigger) {
                        query.hookIds.contains(it.hookId)
                    } else {
                        false
                    }
                }
            }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: TriggerQuery): ULong {
        return projection.filter { query.triggerIds.isEmpty() || it.key in query.triggerIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.funcIds.isEmpty()) true else query.funcIds.contains(it.funcId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .filter {
                if (query.topicIds.isEmpty()) {
                    true
                } else {
                    if (it is EventTrigger) {
                        query.topicIds.contains(it.topicId)
                    } else {
                        false
                    }
                }
            }
            .filter {
                if (query.hookIds.isEmpty()) {
                    true
                } else {
                    if (it is HookTrigger) {
                        query.hookIds.contains(it.hookId)
                    } else {
                        false
                    }
                }
            }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}


class MemoryTriggerRepository : MemoryRecordRepository<TriggerId, TriggerRecord, Trigger>(
    createDomainObject = CreateTriggerFromRecords,
    recordClass = TriggerRecord::class
), TriggerRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateFixedRateCmd): FixedRateTrigger {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as FixedRateTrigger
            }
            store(
                FixedRateTriggerCreatedRecord(
                    cmdId = cmd.id,
                    entityId = triggerId,
                    groupId = cmd.groupId,
                    funcId = cmd.funcId,
                    namespaceId = cmd.namespaceId,
                    name = cmd.name,
                    inputs = cmd.inputs,
                    duration = cmd.duration,
                    correlationId = cmd.correlationId
                )
            )
            (currentVersion(triggerId) as FixedRateTrigger).also(CurrentTriggerProjection::apply)
        }
    }

    override fun create(cmd: CreateEventCmd): EventTrigger {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as EventTrigger
            } else {
                store(
                    EventTriggerCreatedRecord(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        topicId = cmd.topicId,
                        correlationId = cmd.correlationId
                    )
                )
                (currentVersion(triggerId) as EventTrigger).also(CurrentTriggerProjection::apply)
            }
        }
    }

    override fun create(cmd: CreateHookCmd): HookTrigger {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as HookTrigger
            } else {
                store(
                    HookTriggerCreatedRecord(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        hookId = cmd.hookId,
                        hookMethods = cmd.hookMethods,
                        correlationId = cmd.correlationId
                    )
                )
                (currentVersion(triggerId) as HookTrigger).also(CurrentTriggerProjection::apply)
            }
        }
    }

    override fun find(triggerId: TriggerId) = lock.withLock { CurrentTriggerProjection.find(triggerId) }

    override fun list(query: TriggerQuery): List<Trigger> = lock.withLock { CurrentTriggerProjection.list(query) }

    override fun count(query: TriggerQuery): ULong = lock.withLock { CurrentTriggerProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            CurrentTriggerProjection.clear()
        }
    }

    override fun close() {}
}