package io.hamal.backend.repository.record.trigger

import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType
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
    var secrets: TriggerSecrets? = null,

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
                secrets = rec.secrets,
                duration = rec.duration
            )
        }
    }

    override fun toDomainObject(): Trigger {
        return FixedRateTrigger(
            cmdId = cmdId,
            accountId = AccountId(1),
            id = id,
            funcId = funcId!!,
            name = name!!,
            inputs = inputs!!,
            secrets = secrets!!,
            duration = duration!!
        )
    }
}