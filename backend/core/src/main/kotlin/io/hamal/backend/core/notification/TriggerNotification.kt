package io.hamal.backend.core.notification

import io.hamal.backend.core.trigger.AdhocTrigger
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TriggerId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("manual_trigger::created")
data class ManualTriggerCreatedNotification(
    override val shard: Shard,
    val id: TriggerId
) : DomainNotification()

@Serializable
@DomainNotificationTopic("manual_trigger::invoked")
data class ManualTriggerInvokedNotification(
    override val shard: Shard,
    val invokedTrigger: InvokedTrigger.Manual
) : DomainNotification()

@Serializable
@DomainNotificationTopic("adhoc_trigger::invoked")
data class AdhocTriggerInvokedNotification(
    override val shard: Shard,
    val adhocTrigger: AdhocTrigger
) : DomainNotification()