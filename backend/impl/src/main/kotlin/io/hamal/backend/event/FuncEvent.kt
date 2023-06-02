package io.hamal.backend.event

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
@EventTopic("func::created")
data class FuncCreatedEvent(
    override val shard: Shard,
    val funcId: FuncId,
) : Event()
