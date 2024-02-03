package io.hamal.repository.memory.record

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerCmdRepository.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.record.trigger.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private object TriggerCurrentProjection {

    private val projection = mutableMapOf<TriggerId, Trigger>()
    private val uniqueHookTriggers = mutableSetOf<HookTriggerUnique>()

    fun apply(trigger: Trigger) {

        if (trigger.type == TriggerType.Hook) {
            handleHookTrigger(trigger as HookTrigger)
        }

        val currentTrigger = projection[trigger.id]
        projection.remove(trigger.id)

        val triggersInFlow = projection.values.filter { it.flowId == trigger.flowId }
        if (triggersInFlow.any { it.name == trigger.name }) {
            if (currentTrigger != null) {
                projection[currentTrigger.id] = currentTrigger
            }
            throw IllegalArgumentException("${trigger.name} already exists in flow ${trigger.flowId}")
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
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
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
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
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
        uniqueHookTriggers.clear()
    }

    private fun handleHookTrigger(trigger: HookTrigger) {
        val toCheck = HookTriggerUnique(
            trigger.funcId,
            trigger.hookId,
            trigger.hookMethod
        )
        require(uniqueHookTriggers.add(toCheck)) { "Trigger already exists" }
    }

    private data class HookTriggerUnique(
        val funcId: FuncId,
        val hookId: HookId,
        val hookMethod: HookMethod
    )
}


class TriggerMemoryRepository : RecordMemoryRepository<TriggerId, TriggerRecord, Trigger>(
    createDomainObject = CreateTriggerFromRecords,
    recordClass = TriggerRecord::class
), TriggerRepository {

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
                    flowId = cmd.flowId,
                    name = cmd.name,
                    inputs = cmd.inputs,
                    duration = cmd.duration,
                    correlationId = cmd.correlationId,
                    status = cmd.status
                )
            )
            (currentVersion(triggerId) as FixedRateTrigger).also(TriggerCurrentProjection::apply)
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
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        topicId = cmd.topicId,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as EventTrigger).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun create(cmd: CreateHookCmd): HookTrigger {

        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as HookTrigger
            } else {

                TriggerCurrentProjection.list(
                    TriggerQuery(
                        hookIds = listOf(cmd.hookId)
                    )
                ).firstOrNull()?.let { trigger ->
                    trigger as HookTrigger
                    if (trigger.funcId == cmd.funcId && trigger.hookMethod == cmd.hookMethod) {
                        throw IllegalArgumentException("Trigger already exists")
                    }
                }

                store(
                    HookTriggerCreatedRecord(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        hookId = cmd.hookId,
                        hookMethod = cmd.hookMethod,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as HookTrigger).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun create(cmd: CreateCronCmd): CronTrigger {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as CronTrigger
            } else {
                store(
                    CronTriggerCreatedRecord(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        cron = cmd.cron,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as CronTrigger).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun set(triggerId: TriggerId, cmd: SetTriggerStatusCmd): Trigger {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id)
            } else {
                val rec: TriggerRecord = if (cmd.status == TriggerStatus.Active) {
                    TriggerSetActiveRecord(
                        cmdId = cmd.id,
                        entityId = triggerId
                    )
                } else {
                    TriggerSetInactiveRecord(
                        cmdId = cmd.id,
                        entityId = triggerId
                    )
                }
                store(rec)
                currentVersion(triggerId).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun find(triggerId: TriggerId) = lock.withLock { TriggerCurrentProjection.find(triggerId) }

    override fun list(query: TriggerQuery): List<Trigger> = lock.withLock { TriggerCurrentProjection.list(query) }

    override fun count(query: TriggerQuery): ULong = lock.withLock { TriggerCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            TriggerCurrentProjection.clear()
        }
    }

    override fun close() {}

    private val lock = ReentrantLock()
}