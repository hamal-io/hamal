package io.hamal.backend.core.notification

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.lib.vo.Shard
import kotlinx.serialization.Serializable


sealed class FlowDefinitionDomainNotification : DomainNotification() {
    @Serializable
    @DomainNotificationTopic("flow_definition_created")
    data class Created(
        val flowDefinition: FlowDefinition,
        override val shard: Shard
    ) : FlowDefinitionDomainNotification()
}