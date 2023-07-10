package io.hamal.backend.repository.record.trigger

import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
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
    var name: TriggerName? = null,
    var type: TriggerType? = null,
    var inputs: TriggerInputs? = null,

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
                type = FixedRate,
                inputs = rec.inputs,
                duration = rec.duration
            )

            is EventTriggerCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                type = Event,
                inputs = rec.inputs,
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
                name = name!!,
                inputs = inputs!!,
                duration = duration!!
            )

            Event -> EventTrigger(
                cmdId = cmdId,
                id = id,
                funcId = funcId!!,
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