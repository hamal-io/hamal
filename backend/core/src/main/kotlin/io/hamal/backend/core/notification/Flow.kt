package io.hamal.backend.core.notification

import io.hamal.lib.vo.FlowId
import io.hamal.lib.vo.RegionId
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("scheduler::flow_enqueued")
data class Scheduled(
    override val regionId: RegionId,
    val id: FlowId,
    val inputs: Int
) : DomainNotification()


