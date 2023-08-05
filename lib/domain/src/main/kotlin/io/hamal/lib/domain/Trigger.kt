package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface Trigger : DomainObject<TriggerId> {
    val cmdId: CmdId
    val name: TriggerName
    val funcId: FuncId
    val correlationId: CorrelationId?
    val inputs: TriggerInputs
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
) : Trigger

@Serializable
class EventTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    val topicId: TopicId,
    override val correlationId: CorrelationId? = null
) : Trigger
