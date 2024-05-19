package io.hamal.lib.nodes

import io.hamal.lib.common.value.*


class NodeIndex(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun NodeIndex(value: Int) = NodeIndex(ValueNumber(value))
        fun NodeIndex(value: Long) = NodeIndex(ValueNumber(value))
    }
}

class NodeTitle(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun NodeTitle(value: String) = NodeTitle(ValueString(value))
    }
}

class NodeType(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun NodeType(value: String) = NodeType(ValueString(value))
    }
}

class NodeProperties(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

data class Node(
    val index: NodeIndex,
    val type: NodeType,
    val title: NodeTitle,
    val position: Position,
    val size: Size,
    val properties: NodeProperties = NodeProperties(),
    val inputs: List<PortInput> = listOf(),
    val outputs: List<PortOutput> = listOf()
)

