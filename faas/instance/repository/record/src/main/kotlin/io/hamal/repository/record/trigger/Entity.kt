package io.hamal.repository.record.trigger

import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.Trigger
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import kotlin.time.Duration

data class Entity(
    override val id: TriggerId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var funcId: FuncId? = null,
    var namespaceId: NamespaceId? = null,
    var name: TriggerName? = null,
    var type: TriggerType? = null,
    var inputs: TriggerInputs? = null,
    var correlationId: CorrelationId? = null,

    var topicId: TopicId? = null,
    var duration: Duration? = null

) : RecordEntity<TriggerId, TriggerRecord, Trigger> {

    override fun apply(rec: TriggerRecord): Entity {
        return when (rec) {
            is FixedRateTriggerCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
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
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                namespaceId = rec.namespaceId,
                type = Event,
                inputs = rec.inputs,
                correlationId = rec.correlationId,
                topicId = rec.topicId
            )
        }
    }

    override fun toDomainObject(): Trigger {
        return when (type!!) {
            FixedRate -> FixedRateTrigger(
                cmdId = cmdId,
                id = id,
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
                funcId = funcId!!,
                namespaceId = namespaceId!!,
                correlationId = correlationId,
                name = name!!,
                inputs = inputs!!,
                topicId = topicId!!
            )
        }
    }
}

fun List<TriggerRecord>.createEntity(): Entity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()

    check(firstRecord is FixedRateTriggerCreationRecord || firstRecord is EventTriggerCreationRecord)

    var result = Entity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}