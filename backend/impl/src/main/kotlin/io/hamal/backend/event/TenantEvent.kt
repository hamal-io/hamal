package io.hamal.backend.event

import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import kotlinx.serialization.Serializable

@Serializable
class TenantEvent(
    override val reqId: ReqId,
    override val shard: Shard,
    override val topic: String, //FIXME only temp should not be part of the event
    val contentType: String,
    val value: ByteArray
) : Event()