package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("trigger::created")
data class TriggerCreatedEvent(
//    override val reqId: ReqId,
    override val shard: Shard,
    val trigger: Trigger
) : Event()


//@Serializable
//@DomainNotificationTopic("adhoc_trigger::invoked")
//data class AdhocTriggerInvokedEvent(
//    override val shard: Shard,
//    val adhocTrigger: InvokedAdhoc
//) : Event()