package io.hamal.backend.repository.sqlite.record.trigger

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerCmdRepository.CreateFixedRateCmd
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository.Query
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.trigger.Entity
import io.hamal.backend.repository.record.trigger.FixedRateTriggerCreationRecord
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.TriggerId
import java.nio.file.Path

internal object CreateTrigger : CreateDomainObject<TriggerId, TriggerRecord, Trigger> {
    override fun invoke(recs: List<TriggerRecord>): Trigger {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FixedRateTriggerCreationRecord)

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
        override val shard: Shard
    ) : BaseRepository.Config {
        override val filename = "trigger"
    }

    override fun create(cmd: CreateFixedRateCmd): Trigger {
        val triggerId = cmd.triggerId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(triggerId, cmdId)) {
                versionOf(triggerId, cmdId)
            } else {
                storeRecord(
                    FixedRateTriggerCreationRecord(
                        entityId = triggerId,
                        cmdId = cmdId,
                        accountId = AccountId(1),
                        funcId = cmd.funcId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        secrets = cmd.secrets,
                        duration = cmd.duration
                    )
                )

                currentVersion(triggerId)
                    .also { ProjectionCurrent.update(this, it) }
            }
        }
    }

    override fun find(triggerId: TriggerId): Trigger? {
        return ProjectionCurrent.find(connection, triggerId)
    }

    override fun list(afterId: TriggerId, limit: Int): List<Trigger> {
        return ProjectionCurrent.list(connection, afterId, limit)
    }

    override fun query(block: Query.() -> Unit): List<Trigger> {
        val query = Query(
            afterId = TriggerId(0),
            limit = Int.MAX_VALUE
        )
        block(query)
        return ProjectionCurrent.list(connection, query.afterId, query.limit)
    }
}