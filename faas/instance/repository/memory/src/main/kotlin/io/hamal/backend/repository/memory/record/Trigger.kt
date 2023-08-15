package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.TriggerCmdRepository.CreateEventCmd
import io.hamal.backend.repository.api.TriggerCmdRepository.CreateFixedRateCmd
import io.hamal.backend.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.backend.repository.record.trigger.EventTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.FixedRateTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.record.trigger.createEntity
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.TriggerId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentTriggerProjection {

    private val projection = mutableMapOf<TriggerId, Trigger>()

    fun apply(trigger: Trigger) {
        val values = projection.values.groupBy({ it.namespaceId }, { it.name }).toMutableMap()
        values[trigger.namespaceId] = values[trigger.namespaceId]?.plus(trigger.name) ?: listOf(trigger.name)
        val unique = values.all { it.value.size == it.value.toSet().size }
        check(unique) { "Trigger name ${trigger.name} not unique in namespace ${trigger.namespaceId}" }

        projection[trigger.id] = trigger
    }

    fun find(triggerId: TriggerId): Trigger? = projection[triggerId]

    fun list(query: TriggerQuery): List<Trigger> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= query.afterId }
            .mapNotNull { find(it) }
            .filter {
                when (it) {
                    is FixedRateTrigger -> query.types.contains(FixedRate)
                    is EventTrigger -> query.types.contains(Event)
                }
            }
            .take(query.limit.value)
    }

    fun clear() {
        projection.clear()
    }
}


object MemoryTriggerRepository : BaseRecordRepository<TriggerId, TriggerRecord>(),
    TriggerCmdRepository, TriggerQueryRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateFixedRateCmd): FixedRateTrigger {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (contains(triggerId)) {
                versionOf(triggerId, cmd.id) as FixedRateTrigger
            }
            MemoryTriggerRepository.addRecord(
                FixedRateTriggerCreationRecord(
                    entityId = triggerId,
                    cmdId = cmd.id,
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
            if (contains(triggerId)) {
                versionOf(triggerId, cmd.id) as EventTrigger
            } else {
                MemoryTriggerRepository.addRecord(
                    EventTriggerCreationRecord(
                        entityId = triggerId,
                        cmdId = cmd.id,
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

    override fun find(triggerId: TriggerId) = CurrentTriggerProjection.find(triggerId)

    override fun list(block: TriggerQuery.() -> Unit): List<Trigger> {
        val query = TriggerQuery().also(block)
        return CurrentTriggerProjection.list(query)
    }

    override fun clear() {
        super.clear()
        CurrentTriggerProjection.clear()
    }
}

private fun MemoryTriggerRepository.currentVersion(id: TriggerId): Trigger {
    return listRecords(id)
        .createEntity()
        .toDomainObject()
}

private fun MemoryTriggerRepository.versionOf(id: TriggerId, cmdId: CmdId): Trigger {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}