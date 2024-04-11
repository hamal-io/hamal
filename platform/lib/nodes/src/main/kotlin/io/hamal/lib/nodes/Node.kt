package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.nodes.control.Control


class NodeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class NodeTitle(override val value: String) : ValueObjectString()

class NodeType(override val value: String) : ValueObjectString()

data class Node(
    val id: NodeId,
    val type: NodeType,
    val title: NodeTitle,
    val position: Position,
    val size: Size,
    val controls: List<Control> = listOf(),
    val outputs: List<PortOutput> = listOf()
)

interface NodeRegistry {

    operator fun get(type: NodeType): Item

    interface Item {
        val type: NodeType
        val title: NodeTitle
        val size: Size
//    val control: List<ControlTemplate>
//    val outputs: List<NodeOutput>

        fun toCode(): KuaCode
    }

}

