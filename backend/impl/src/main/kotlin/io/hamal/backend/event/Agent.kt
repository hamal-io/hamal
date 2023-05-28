package io.hamal.backend.event

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("agent::completed")
data class AgentCompletedEvent(
    override val shard: Shard,
    val execId: ExecId,
    val statePayload: StatePayload
) : Event()