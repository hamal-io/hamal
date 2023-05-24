package io.hamal.backend.event

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("func::created")
data class FuncCreatedEvent(
    override val shard: Shard,
    val id: FuncId,
) : Event()
