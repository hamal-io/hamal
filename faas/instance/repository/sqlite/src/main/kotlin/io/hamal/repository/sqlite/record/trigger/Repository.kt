package io.hamal.repository.sqlite.record.trigger

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.TriggerCmdRepository.CreateFixedRateCmd
import io.hamal.backend.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.trigger.Entity
import io.hamal.repository.record.trigger.EventTriggerCreationRecord
import io.hamal.repository.record.trigger.FixedRateTriggerCreationRecord
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.BaseSqliteRepository
import java.nio.file.Path

internal object CreateTrigger : CreateDomainObject<TriggerId, TriggerRecord, Trigger> {
    override fun invoke(recs: List<TriggerRecord>): Trigger {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()

        check(firstRecord is FixedRateTriggerCreationRecord || firstRecord is EventTriggerCreationRecord)

        var result = Entity(
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
    ) : BaseSqliteRepository.Config {
        override val filename = "trigger.db"
    }

    override fun create(cmd: CreateFixedRateCmd): FixedRateTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(triggerId, cmdId)) {
                versionOf(triggerId, cmdId) as FixedRateTrigger
            } else {
                storeRecord(
                    FixedRateTriggerCreationRecord(
                        entityId = triggerId,
                        cmdId = cmdId,
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

    override fun create(cmd: TriggerCmdRepository.CreateEventCmd): EventTrigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(triggerId, cmdId)) {
                versionOf(triggerId, cmdId) as EventTrigger
            } else {
                storeRecord(
                    EventTriggerCreationRecord(
                        entityId = triggerId,
                        cmdId = cmdId,
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

    override fun find(triggerId: TriggerId): Trigger? {
        return ProjectionCurrent.find(connection, triggerId)
    }

    override fun list(block: TriggerQuery.() -> Unit): List<Trigger> {
        val query = TriggerQuery().also(block)
        return ProjectionCurrent.list(connection, query)
    }
}