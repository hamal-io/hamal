package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.trigger.AdhocTrigger
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TriggerId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("trigger::created")
data class TriggerCreatedEvent(
    override val shard: Shard,
    val id: TriggerId,
) : Event()


@Serializable
@DomainNotificationTopic("adhoc_trigger::invoked")
data class AdhocTriggerInvokedEvent(
    override val shard: Shard,
    val adhocTrigger: AdhocTrigger
) : Event()