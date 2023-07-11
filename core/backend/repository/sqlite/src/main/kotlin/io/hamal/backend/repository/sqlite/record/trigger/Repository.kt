package io.hamal.backend.repository.sqlite.record.trigger

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerCmdRepository.CreateFixedRateCmd
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.trigger.Entity
import io.hamal.backend.repository.record.trigger.EventTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.FixedRateTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
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
    projections = listOf(
        ProjectionCurrent
    )
), TriggerCmdRepository, TriggerQueryRepository {

    data class Config(
        override val path: Path,
    ) : BaseSqliteRepository.Config {
        override val filename = "trigger"
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
                        name = cmd.name,
                        inputs = cmd.inputs,
                        duration = cmd.duration
                    )
                )

                (currentVersion(triggerId) as FixedRateTrigger)
                    .also { ProjectionCurrent.update(this, it) }
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
                        name = cmd.name,
                        inputs = cmd.inputs,
                        topicId = cmd.topicId
                    )
                )

                (currentVersion(triggerId) as EventTrigger)
                    .also { ProjectionCurrent.update(this, it) }
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