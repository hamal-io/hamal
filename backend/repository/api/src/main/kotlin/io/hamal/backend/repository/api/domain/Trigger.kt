package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed class Trigger : DomainObject<TriggerId>() {
    abstract val name: TriggerName
    abstract val funcId: FuncId
    abstract val type: TriggerType
}

@Serializable
class FixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    val duration: Duration
) : Trigger() {
    override val type = FixedRate
}

@Serializable
class EventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    val topicId: TopicId
) : Trigger() {
    override val type = Event
}
