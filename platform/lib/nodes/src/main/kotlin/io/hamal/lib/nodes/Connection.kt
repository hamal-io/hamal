package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString

class ConnectionId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class ConnectionLabel(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ConnectionLabel(value: String) = ConnectionLabel(ValueString(value))
    }
}

data class Connection(
    val id: ConnectionId,
    val outputNode: Node,
    val outputPort: Port,
    val inputNode: Node,
    val inputPort: Port,
    val label: ConnectionLabel? = null
) {
    init {
        // FIXME ensure connection is valid
    }


    data class Node(val id: NodeId)
    data class Port(val id: PortId)
}