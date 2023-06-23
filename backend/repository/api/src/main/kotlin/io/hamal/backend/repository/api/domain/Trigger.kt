package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.DomainObject
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
    abstract val type: TriggerType
    abstract val inputs: TriggerInputs
    abstract val secrets: TriggerSecrets
}

@Serializable
class FixedRateTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs,
    override val secrets: TriggerSecrets,
    val duration: Duration
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
    override val secrets: TriggerSecrets,
    val topicId: TopicId
) : Trigger() {
    override val type = Event
}
