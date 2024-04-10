package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.nodes.node.NodeId

class ConnectionId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

data class Connection(
    val id: ConnectionId,
    val outputNode: Node,
    val outputPort: Port,
    val inputNode: Node,
    val inputPort: Port
) {
    init {
        // FIXME ensure connection is valid
    }


    data class Node(val id: NodeId)
    data class Port(val id: PortId)
}