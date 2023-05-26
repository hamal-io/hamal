package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("trigger::created")
data class TriggerCreatedEvent(
    override val shard: Shard,
    val trigger: Trigger
) : Event()


//@Serializable
//@DomainNotificationTopic("adhoc_trigger::invoked")
//data class AdhocTriggerInvokedEvent(
//    override val shard: Shard,
//    val adhocTrigger: InvokedAdhoc
//) : Event()