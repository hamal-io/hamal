package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerCmdRepository.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.trigger.*
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateTrigger : CreateDomainObject<TriggerId, TriggerRecord, Trigger> {
    override fun invoke(recs: List<TriggerRecord>): Trigger {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()

        check(firstRecord is FixedRateTriggerCreationRecord || firstRecord is EventTriggerCreationRecord || firstRecord is HookTriggerCreationRecord)

        var result = TriggerEntity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteTriggerRepository(
    config: Config
) : SqliteRecordRepository<TriggerId, TriggerRecord, Trigger>(
    config = config,
    createDomainObject = CreateTrigger,
    recordClass = TriggerRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), TriggerRepository {

    data class Config(
        override val path: Path,
    ) : SqliteBaseRepository.Config {
        override val filename = "trigger.db"
    }

    override fun create(cmd: CreateFixedRateCmd): FixedRateTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, triggerId)) {
                versionOf(triggerId, cmdId) as FixedRateTrigger
            } else {
                store(
                    FixedRateTriggerCreationRecord(
                        cmdId = cmdId,
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

                (currentVersion(triggerId) as FixedRateTrigger)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun create(cmd: CreateEventCmd): EventTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, triggerId)) {
                versionOf(triggerId, cmdId) as EventTrigger
            } else {
                store(
                    EventTriggerCreationRecord(
                        cmdId = cmdId,
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

                (currentVersion(triggerId) as EventTrigger)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun create(cmd: CreateHookCmd): HookTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, triggerId)) {
                versionOf(triggerId, cmdId) as HookTrigger
            } else {
                store(
                    HookTriggerCreationRecord(
                        cmdId = cmdId,
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

                (currentVersion(triggerId) as HookTrigger)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(triggerId: TriggerId): Trigger? {
        return ProjectionCurrent.find(connection, triggerId)
    }

    override fun list(query: TriggerQuery): List<Trigger> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: TriggerQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}