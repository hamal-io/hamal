package io.hamal.backend.event

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("func::created")
data class FuncCreatedEvent(
    override val shard: Shard,
    val id: FuncId,
) : Event()
