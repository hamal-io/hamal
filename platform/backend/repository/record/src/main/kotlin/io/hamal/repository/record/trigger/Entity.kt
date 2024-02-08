package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt
import kotlin.time.Duration


data class TriggerEntity(
    override val id: TriggerId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var groupId: GroupId? = null,
    var funcId: FuncId? = null,
    var namespaceId: NamespaceId? = null,
    var name: TriggerName? = null,
    var type: TriggerType? = null,
    var inputs: TriggerInputs? = null,
    var correlationId: CorrelationId? = null,

    var topicId: TopicId? = null,
    var duration: Duration? = null,
    var hookId: HookId? = null,
    var hookMethod: HookMethod? = null,

    var cron: CronPattern? = null,

    var status: TriggerStatus? = null

) : RecordEntity<TriggerId, TriggerRecord, Trigger> {

    override fun apply(rec: TriggerRecord): TriggerEntity {
        return when (rec) {
            is FixedRateTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                namespaceId = rec.namespaceId,
                type = FixedRate,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                duration = rec.duration,
                recordedAt = rec.recordedAt(),
                status = rec.status
            )

            is EventTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                namespaceId = rec.namespaceId,
                type = Event,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                topicId = rec.topicId,
                recordedAt = rec.recordedAt(),
                status = rec.status
            )

            is HookTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                namespaceId = rec.namespaceId,
                type = Hook,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                hookId = rec.hookId,
                hookMethod = rec.hookMethod,
                recordedAt = rec.recordedAt(),
                status = rec.status
            )

            is CronTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                namespaceId = rec.namespaceId,
                type = Cron,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                cron = rec.cron,
                recordedAt = rec.recordedAt(),
                status = rec.status
            )

            is TriggerSetActiveRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                status = TriggerStatus.Active
            )

            is TriggerSetInactiveRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                status = TriggerStatus.Inactive
            )
        }
    }

    override fun toDomainObject(): Trigger {
        return when (type!!) {
            FixedRate -> FixedRateTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                duration = duration!!,
                status = status!!
            )

            Event -> EventTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                topicId = topicId!!,
                status = status!!
            )

            Hook -> HookTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                hookId = hookId!!,
                hookMethod = hookMethod!!,
                status = status!!
            )

            Cron -> CronTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                cron = cron!!,
                status = status!!
            )
        }
    }
}

fun List<TriggerRecord>.createEntity(): TriggerEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord: TriggerRecord = first()

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

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateTriggerFromRecords : CreateDomainObject<TriggerId, TriggerRecord, Trigger> {
    override fun invoke(recs: List<TriggerRecord>): Trigger {
        return recs.createEntity().toDomainObject()
    }
}