package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed class Trigger : DomainObject<TriggerId> {
    abstract val cmdId: CmdId
    abstract val name: TriggerName
    abstract val funcId: FuncId
    abstract val correlationId: CorrelationId?
    abstract val type: TriggerType
    abstract val inputs: TriggerInputs
}

@Serializable
class FixedRateTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    val duration: Duration,
    override val correlationId: CorrelationId? = null
) : Trigger() {
    override val type = FixedRate
}

@Serializable
class EventTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    val topicId: TopicId,
    override val correlationId: CorrelationId? = null
) : Trigger() {
    override val type = Event
}
