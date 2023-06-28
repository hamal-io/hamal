package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.backend.repository.record.trigger.EventTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.FixedRateTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.record.trigger.createEntity
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.domain.vo.TriggerId

internal object CurrentTriggerProjection {
    private val projection = mutableMapOf<TriggerId, Trigger>()
    fun apply(trigger: Trigger) {
        projection[trigger.id] = trigger
    }

    fun find(triggerId: TriggerId): Trigger? = projection[triggerId]

    fun list(afterId: TriggerId, limit: Limit): List<Trigger> {
        return projection.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
            .reversed()
    }

    fun clear() {
        projection.clear()
    }
}


object MemoryTriggerRepository : BaseRecordRepository<TriggerId, TriggerRecord>(),
    TriggerCmdRepository, TriggerQueryRepository {
    override fun create(cmd: TriggerCmdRepository.CreateFixedRateCmd): FixedRateTrigger {
        val triggerId = cmd.triggerId
        if (contains(triggerId)) {
            return versionOf(triggerId, cmd.id) as FixedRateTrigger
        }
        MemoryTriggerRepository.addRecord(
            FixedRateTriggerCreationRecord(
                entityId = triggerId,
                cmdId = cmd.id,
                funcId = cmd.funcId,
                name = cmd.name,
                inputs = cmd.inputs,
                secrets = cmd.secrets,
                duration = cmd.duration
            )
        )
        return (currentVersion(triggerId) as FixedRateTrigger)
            .also(CurrentTriggerProjection::apply)
    }

    override fun create(cmd: TriggerCmdRepository.CreateEventCmd): EventTrigger {
        val triggerId = cmd.triggerId
        if (contains(triggerId)) {
            return versionOf(triggerId, cmd.id) as EventTrigger
        }
        MemoryTriggerRepository.addRecord(
            EventTriggerCreationRecord(
                entityId = triggerId,
                cmdId = cmd.id,
                funcId = cmd.funcId,
                name = cmd.name,
                inputs = cmd.inputs,
                secrets = cmd.secrets,
                topicId = cmd.topicId
            )
        )
        return (currentVersion(triggerId) as EventTrigger)
            .also(CurrentTriggerProjection::apply)
    }

    override fun find(triggerId: TriggerId) = CurrentTriggerProjection.find(triggerId)

    override fun list(block: TriggerQuery.() -> Unit): List<Trigger> {
        val query = TriggerQuery().also(block)
        return CurrentTriggerProjection.list(query.afterId, query.limit)
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

private fun MemoryTriggerRepository.commandAlreadyApplied(id: TriggerId, cmdId: CmdId) =
    listRecords(id).any { it.cmdId == cmdId }

private fun MemoryTriggerRepository.versionOf(id: TriggerId, cmdId: CmdId): Trigger {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}