package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain._enum.TriggerStatus
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

        check(
            firstRecord is FixedRateTriggerCreatedRecord ||
                    firstRecord is EventTriggerCreatedRecord ||
                    firstRecord is HookTriggerCreatedRecord ||
                    firstRecord is CronTriggerCreatedRecord
        )

        var result = TriggerEntity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
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
                    FixedRateTriggerCreatedRecord(
                        cmdId = cmdId,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        duration = cmd.duration,
                        status = cmd.status,
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
                    EventTriggerCreatedRecord(
                        cmdId = cmdId,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        topicId = cmd.topicId,
                        status = cmd.status,
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
                    HookTriggerCreatedRecord(
                        cmdId = cmdId,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        hookId = cmd.hookId,
                        hookMethods = cmd.hookMethods,
                        status = cmd.status,
                        correlationId = cmd.correlationId
                    )
                )

                (currentVersion(triggerId) as HookTrigger)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun create(cmd: CreateCronCmd): CronTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, triggerId)) {
                versionOf(triggerId, cmdId) as CronTrigger
            } else {
                store(
                    CronTriggerCreatedRecord(
                        cmdId = cmdId,
                        entityId = triggerId,
                        groupId = cmd.groupId,
                        funcId = cmd.funcId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        cron = cmd.cron,
                        status = cmd.status,
                        correlationId = cmd.correlationId
                    )
                )

                (currentVersion(triggerId) as CronTrigger)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun set(triggerId: TriggerId, cmd: SetTriggerStatusCmd): Trigger {
        return tx {
            if (commandAlreadyApplied(cmd.id, triggerId)) {
                versionOf(triggerId, cmd.id)
            } else {
                if (cmd.status == TriggerStatus.Active) {
                    store(
                        TriggerSetActiveRecord(
                            cmdId = cmd.id,
                            entityId = triggerId
                        )
                    )
                } else {
                    store(
                        TriggerSetInactiveRecord(
                            cmdId = cmd.id,
                            entityId = triggerId
                        )
                    )
                }
                (currentVersion(triggerId))
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