package io.hamal.backend.core.notification

import io.hamal.backend.core.model.Trigger
import io.hamal.lib.vo.RegionId
import kotlinx.serialization.Serializable

sealed class TriggerDomainNotification : DomainNotification() {
    @Serializable
    @DomainNotificationTopic("trigger_created")
    data class Created(
        val trigger: Trigger,
        override val regionId: RegionId
    ) : TriggerDomainNotification()
}