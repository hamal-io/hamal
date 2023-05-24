package io.hamal.backend.core.notification

import io.hamal.backend.core.trigger.AdhocTrigger
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TriggerId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("adhoc_trigger::invoked")
data class AdhocTriggerInvokedNotification(
    override val shard: Shard,
    val adhocTrigger: AdhocTrigger
) : DomainNotification()