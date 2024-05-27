package io.hamal.lib.nodes

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableNumber
import io.hamal.lib.common.value.ValueVariableString


class ConnectionIndex(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun ConnectionIndex(value: Int) = ConnectionIndex(ValueNumber(value))
        fun ConnectionIndex(value: Long) = ConnectionIndex(ValueNumber(value))
    }

    override fun toString(): String = value.longValue.toString()
}


class ConnectionLabel(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ConnectionLabel(value: String) = ConnectionLabel(ValueString(value))
    }
}

data class Connection(
    val index: ConnectionIndex,
    val outputNode: Node,
    val outputPort: Port,
    val inputNode: Node,
    val inputPort: Port,
    val label: ConnectionLabel? = null
) {
    init {
        // FIXME ensure connection is valid
    }


    data class Node(val index: NodeIndex)
    data class Port(val index: PortIndex, val key: PortKey)
}