package io.hamal.backend.event

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.Serializable


@Serializable
@EventTopic("agent::failed")
data class AgentFailedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val execId: ExecId
) : Event()