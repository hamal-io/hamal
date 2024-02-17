package io.hamal.repository.memory.record.trigger

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.TriggerRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.trigger.CreateTriggerFromRecords
import io.hamal.repository.record.trigger.TriggerRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class TriggerMemoryRepository : RecordMemoryRepository<TriggerId, TriggerRecord, Trigger>(
    createDomainObject = CreateTriggerFromRecords,
    recordClass = TriggerRecord::class
), TriggerRepository {

    override fun create(cmd: CreateFixedRateCmd): Trigger.FixedRate {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as Trigger.FixedRate
            }
            store(
                TriggerRecord.FixedRateCreated(
                    cmdId = cmd.id,
                    entityId = triggerId,
                    workspaceId = cmd.workspaceId,
                    funcId = cmd.funcId,
                    namespaceId = cmd.namespaceId,
                    name = cmd.name,
                    inputs = cmd.inputs,
                    duration = cmd.duration,
                    correlationId = cmd.correlationId,
                    status = cmd.status
                )
            )
            (currentVersion(triggerId) as Trigger.FixedRate).also(TriggerCurrentProjection::apply)
        }
    }

    override fun create(cmd: CreateEventCmd): Trigger.Event {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as Trigger.Event
            } else {
                store(
                    TriggerRecord.EventCreated(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        workspaceId = cmd.workspaceId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        topicId = cmd.topicId,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as Trigger.Event).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun create(cmd: CreateHookCmd): Trigger.Hook {

        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as Trigger.Hook
            } else {

                TriggerCurrentProjection.list(
                    TriggerQuery(
                        hookIds = listOf(cmd.hookId)
                    )
                ).firstOrNull()?.let { trigger ->
                    trigger as Trigger.Hook
                    if (trigger.funcId == cmd.funcId && trigger.hookMethod == cmd.hookMethod) {
                        throw IllegalArgumentException("Trigger already exists")
                    }
                }

                store(
                    TriggerRecord.HookCreated(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        workspaceId = cmd.workspaceId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        hookId = cmd.hookId,
                        hookMethod = cmd.hookMethod,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as Trigger.Hook).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun create(cmd: CreateCronCmd): Trigger.Cron {
        return lock.withLock {
            val triggerId = cmd.triggerId
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id) as Trigger.Cron
            } else {
                store(
                    TriggerRecord.CronCreated(
                        cmdId = cmd.id,
                        entityId = triggerId,
                        workspaceId = cmd.workspaceId,
                        funcId = cmd.funcId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        cron = cmd.cron,
                        correlationId = cmd.correlationId,
                        status = cmd.status
                    )
                )
                (currentVersion(triggerId) as Trigger.Cron).also(TriggerCurrentProjection::apply)
            }
        }
    }

    override fun set(triggerId: TriggerId, cmd: SetTriggerStatusCmd): Trigger {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id)
            } else {
                val rec: TriggerRecord = if (cmd.status == TriggerStatus.Active) {
                    TriggerRecord.SetActive(
                        cmdId = cmd.id,
                        entityId = triggerId
                    )
                } else {
                    TriggerRecord.SetInactive(
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

    override fun count(query: TriggerQuery): Count = lock.withLock { TriggerCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            TriggerCurrentProjection.clear()
        }
    }

    override fun close() {}

    private val lock = ReentrantLock()
}