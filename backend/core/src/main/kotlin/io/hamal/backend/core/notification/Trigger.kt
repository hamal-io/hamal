package io.hamal.backend.core.notification

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.model.Trigger
import io.hamal.lib.Shard
import kotlinx.serialization.Serializable

sealed class TriggerDomainNotification : DomainNotification() {
    @Serializable
    @DomainNotificationTopic("trigger_created")
    data class Created(
        val trigger: Trigger,
        override val shard: Shard
    ) : TriggerDomainNotification()

    @Serializable
    @DomainNotificationTopic("trigger_invoked")
    data class Invoked(
        val invokedTrigger: InvokedTrigger,
        override val shard: Shard
    ) : DomainNotification()
}