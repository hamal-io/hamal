package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId

class ConnectionId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

data class Connection(
    val id: ConnectionId,

    val inputNodeId: NodeId,
    val inputSlotId: SlotId,

    val outputNodeId: NodeId,
    val outputSlotId: SlotId
) {
    init {
        // FIXME ensure connection is valid
    }
}