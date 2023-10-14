package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.*
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.Trigger

import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import kotlin.time.Duration


data class TriggerEntity(
    override val id: TriggerId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var groupId: GroupId? = null,
    var funcId: FuncId? = null,
    var namespaceId: NamespaceId? = null,
    var name: TriggerName? = null,
    var type: TriggerType? = null,
    var inputs: TriggerInputs? = null,
    var correlationId: CorrelationId? = null,

    var topicId: TopicId? = null,
    var duration: Duration? = null,
    var hookId: HookId? = null

) : RecordEntity<TriggerId, TriggerRecord, Trigger> {

    override fun apply(rec: TriggerRecord): TriggerEntity {
        return when (rec) {
            is FixedRateTriggerCreationRecord -> copy(
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
                duration = rec.duration
            )

            is EventTriggerCreationRecord -> copy(
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
                topicId = rec.topicId
            )

            is HookTriggerCreationRecord -> copy(
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
                hookId = rec.hookId
            )
        }
    }

    override fun toDomainObject(): Trigger {
        return when (type!!) {
            FixedRate -> FixedRateTrigger(
                cmdId = cmdId,
                id = id,
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                duration = duration!!
            )

            Event -> EventTrigger(
                cmdId = cmdId,
                id = id,
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                topicId = topicId!!
            )

            Hook -> HookTrigger(
                cmdId = cmdId,
                id = id,
                groupId = groupId!!,
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                hookId = hookId!!
            )
        }
    }
}

fun List<TriggerRecord>.createEntity(): TriggerEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()

    check(firstRecord is FixedRateTriggerCreationRecord || firstRecord is EventTriggerCreationRecord || firstRecord is HookTriggerCreationRecord)

    var result = TriggerEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
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