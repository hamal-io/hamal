package io.hamal.backend.event

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.Serializable


@Serializable
@SystemEventTopic("agent::failed")
data class AgentFailedEvent(
    override val reqId: ReqId,
    override val shard: Shard,
    val execId: ExecId
) : Event()