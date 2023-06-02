package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import kotlinx.serialization.Serializable

@Serializable
@EventTopic("trigger::created")
data class TriggerCreatedEvent(
    override val shard: Shard,
    val trigger: Trigger
) : Event()
