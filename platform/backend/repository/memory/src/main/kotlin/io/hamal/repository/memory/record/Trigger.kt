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
                FixedRateTriggerCreationRecord(
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
                    EventTriggerCreationRecord(
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
                    HookTriggerCreationRecord(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        hookId = cmd.hookId,
                        correlationId = cmd.correlationId
                    )
                )
                (currentVersion(triggerId) as HookTrigger).also(CurrentTriggerProjection::apply)
            }
        }
    }

    override fun find(triggerId: TriggerId) = CurrentTriggerProjection.find(triggerId)

    override fun list(query: TriggerQuery): List<Trigger> {
        return CurrentTriggerProjection.list(query)
    }

    override fun count(query: TriggerQuery): ULong {
        return CurrentTriggerProjection.count(query)
    }

    override fun clear() {
        super.clear()
        CurrentTriggerProjection.clear()
    }

    override fun close() {}
}