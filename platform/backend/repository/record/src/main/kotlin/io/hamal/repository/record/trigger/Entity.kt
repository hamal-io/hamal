package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import kotlin.time.Duration


data class TriggerEntity(
    override val id: TriggerId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var groupId: GroupId? = null,
    var funcId: FuncId? = null,
    var flowId: FlowId? = null,
    var name: TriggerName? = null,
    var type: TriggerType? = null,
    var inputs: TriggerInputs? = null,
    var correlationId: CorrelationId? = null,

    var topicId: TopicId? = null,
    var duration: Duration? = null,
    var hookId: HookId? = null,
    var hookMethod: HookMethod? = null,

    var cron: CronPattern? = null

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
                flowId = rec.flowId,
                type = FixedRate,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                duration = rec.duration,
                recordedAt = rec.recordedAt()
            )

            is EventTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                flowId = rec.flowId,
                type = Event,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                topicId = rec.topicId,
                recordedAt = rec.recordedAt()
            )

            is HookTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                flowId = rec.flowId,
                type = Hook,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                hookId = rec.hookId,
                hookMethod = rec.hookMethod,
                recordedAt = rec.recordedAt()
            )

            is CronTriggerCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                flowId = rec.flowId,
                type = Cron,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                cron = rec.cron,
                recordedAt = rec.recordedAt()
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
                flowId = flowId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                duration = duration!!
            )

            Event -> EventTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                flowId = flowId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                topicId = topicId!!
            )

            Hook -> HookTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                flowId = flowId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                hookId = hookId!!,
                hookMethod = hookMethod!!
            )

            Cron -> CronTrigger(
                cmdId = cmdId,
                id = id,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                funcId = funcId!!,
                flowId = flowId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                cron = cron!!
            )
        }
    }
}

fun List<TriggerRecord>.createEntity(): TriggerEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()

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