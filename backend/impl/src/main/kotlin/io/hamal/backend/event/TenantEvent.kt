package io.hamal.backend.event

import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable

@Serializable
class TenantEvent(
    override val shard: Shard,
    override val topic: String //FIXME only temp should be part of the event
) : Event()