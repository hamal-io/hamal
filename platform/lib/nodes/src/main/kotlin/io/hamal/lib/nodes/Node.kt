package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*


class NodeId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun NodeId(value: SnowflakeId) = NodeId(ValueSnowflakeId(value))
        fun NodeId(value: Int) = NodeId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun NodeId(value: String) = NodeId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
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
    val id: NodeId,
    val type: NodeType,
    val title: NodeTitle,
    val position: Position,
    val size: Size,
    val properties: NodeProperties = NodeProperties(),
    val outputs: List<PortOutput> = listOf()
)

data class TemplateNode(
    val type: NodeType,
    val title: NodeTitle,
    val size: Size,
    val controls: List<TemplateControl>,
    val outputs: List<TemplatePortOutput>
)